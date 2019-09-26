# 인터넷뱅킹 이용현황 정보

데이터셋 : http://data.seoul.go.kr/dataList/datasetView.do?infId=10909&srvType=S&serviceKind=2&c
## 개발 프레임워크
### Software 구성
* 개발언어: Java SE 12
* Framework: Spring Boot 2.1.8
* Database: MongoDB

## 문제 해결 전략
### 요구사항 분석
* 기본 정책(확장성 여부 등)
> 요구사항을 만족하며, 요구사항에 기술되지 않은 확장성은 고려하지 않았다.
> 
* 입력데이터
> 이번 요구사항은 통계데이터에 대한 입력 및 출력이다. 데이터는 년간 데이터이므로 빈번한 변경이 이뤄지지 않고 년도별 통계 데이터이므로 최대 1년에 한번 업데이트된다.
* 출력
> REST API 의 경우에 인증정책을 포함하나 사용자 식별 데이터가 아니라 정보 접근만 허용할뿐 데이터는 사용자 식별되지 않는 데이터를 제공한다.
> 때문에 출력 데이터는 캐시를 이용하여 사용자간에 공유할 수 있도록 제공한다.

* 성능 이슈

> 10,000TPS 이상의 성능을 나타내기 위하여, 캐시를 추가하였으며 상용환경에서는 이러한 변경되지 않는 정보는 CDN 등을 이용하여 서버 부하 및 응답속도 향상을 이룰 수 있다. 
현재 코드에서는 컨트롤러에 ehCache 두어 동일한 요청이 올경우 바로 응답하도록 한다. 캐시의 TTL 값은 데이터가 빈번하게 변경되지 않으므로 2주로 하였다.
> 만약 2주내에 데이터가 변경되고 그것이 실시간에 반영된다는 요구사항이 추가로 발생할 경우 JMS 를 이용하여 캐시를 PURGE 하여 다시 데이터베이스로부터 값을 얻어 올수 있도록 한다. 

      
### 데이터 Collections
입력데이터(DATASET)와 출력데이터를 분석하여 데이터 컬렉션은 아래와 같이 두개의 컬렉션으로 나누어 저장한다.
* Device: 단말기 헤더 코드
* Connection: 연도별 사용률 정보

#### Device
```typescript
interface Device {
  "device_id": string;
  "device_name": string;  
}
```
#### Connection
년도별 단말기별 사용률 정보를 나타낸다.
```typescript
inteface Connection {
    "id": string;
    "year": number;
    "useRate": float;
   "device_id": string;
   "rate": float;
}
```
>
### API 기능 명세
|URL|설명|Parameters|Method|
|---|----|----|----|
|/auth/signup|회원가입| |POST|
|/auth/login|로그인| |POST|
|/auth/refresh|토큰재요청| |GET|
|/api/code-device/device|서비스 접속 기기 목록을 출력| |GET|
|/api/device-stat/by-year|각 년도별로 인터넷뱅킹을 가장 많이 이용하는 접속기기를 출력| |GET|
|/api/device-stat/year/{year}|특정 년도를 입력받아 그 해에 인터넷뱅킹에 가장 많이 접속하는 기기 이름을 출력| year: Integer, 연도(예, 2018)|GET|
|/api/device-stat/device/{device}|디바이스 아이디를 입력받아 인터넷뱅킹에 접속 비율이 가장 많은 해를 출력| device: string, 단말기ID(예, DEVICE_00000)|GET|
#### 기본 문제
- 데이터 파일(서울시 인터넷뱅킹 이용률 및 이용기기 통계 데이터)에서 각 레코드를 데이터베이스에
   저장하는 코드를 구현하세요.
   
서비스가 기동되면 DeviceStatCSVParser 컴포넌트가 생성된후 Spring의 @PostConstruct 어노테이션이 실행된다.
기동된 후 sample.csv.saved 라는 파일이 생성되지 않았으면, 데이터베이스에 저장한다. 서버가 다시 기동된 후 파일이 존재하면 CSV 를 저장하지 않는다. 
```java
    private boolean isWritten() {
        File f = new File(filename + WRITTEN_SUFFIX);
        return f.exists();
    }
```
CSV 파서는 org.apache.commons 에서 제공하는 csv 파서를 사용한다.
```
implementation 'org.apache.commons:commons-csv:1.7'
```

CVS 파일은 헤더와 년도별 데이터를 구분하여 저장한다.   
```java
public void save()  throws DeviceStatException {
    ...중략
    try {
        CSVParser csvParser = loadCSV();
        try {
            devices = deviceRepository.insert(parseHeaders(csvParser.getHeaderNames()));
            connectionRepository.insert(parseBody(csvParser.getRecords()));

            if (false == createWrittenFile()) {
                log.warn("'{}{}' cannot create.", filename, WRITTEN_SUFFIX);
            }
        } finally {
            close(csvParser);
        }
    } catch ( IOException ex ) {
        log.error("SAVE ERROR", ex);
        throw new DeviceStatException(DeviceStatException.CANNOT_SAVE_DB);
    }
}
    
```
헤더는 DEVICE 컬랙션에 저장하고, 년도별 접속률은 CONNECTION 컬렉션에 저장한다.
컬렉션에 저장할때는 년도별 접속기기에 따라 나타난 접속률을 앞서 언급한 년도별 단말기별 사용률 정보에 따라 단말기, 연도별을 하나의 document 로 구분하여 저장한다.
이렇게 저장하는 이유는 요구사항이 연도별, 단말기별로 데이터를 산출해야 하기때문에 그에 용이한 데이터 구조를 갖는다.   
   
저장이 완료되면 sample.csv.saved 라는 파일을 생성하여 재기동하더라도 데이터베이스에 저장하는 로직이 다시 실행되지 않도록 하였다.

----

- 인터넷뱅킹 서비스 접속 기기 목록을 출력하는 API 를 개발하세요.
```
{
    "devices" : [
        { “device_id”: “DIS7864654”,
            “device_name”: “스마트폰”},
        { “device_id”: “DIS231434”,
            “device_name”: “데스크탑 컴퓨터”},
         ...
        { "device_id”: “DIS645389”,
            “device_name”: “스마트패드”}
    ]
}
```
> 데이터 베이스의 컬렉션은 단말기 코드 목록과 접속기록 목록으로 구분되어 저장한다.
> 접속 기기 목록은 단말기 코드 목록을 모두 조회여 얻을 수 있다.
> 단말기ID 는 DEVICE0000# 형식으로 # 는 0부터 숫자가 오름차순으로 생성된다.

- 각 년도별로 인터넷뱅킹을 가장 많이 이용하는 접속기기를 출력하는 API 를 개발하세요.
```
{
    “devices” : [
        {   “year”: 2011,
            “device_id”: “DIS231434”,
            “device_name”: “데스크탑 컴퓨터”,
            “rate”: 95.1 },
        {   “year”: 2012,
            “device_id”: “DIS231434”,
            “device_name”: “데스크탑 컴퓨터”
            “rate”: 93.9 },
        ...
        {   “year”: 2018,
            "device_id”: “DIS936595”,
            “device_name”: “스마트폰”,
            “rate”: 90.5 }
    ]
}
```
데이터를 가져오기에 앞서 DEVICE 컬렉션에 저장된 항목은 코드 테이블로 고려하여 사전에 미리 로드해 놓는다.
몽고디비 특성상 JOIN 을 선호하지 않아 두개의 트랜젝션을 이용하여 해당 요청을 구성한다.
1. 연도별로 최대 접속률을 가지고 있는 이용률을 검색한다.
```java
AggregationResults<Document> obj = this.mongoTemplate.aggregate(
        newAggregation(
                group(
                        Fields.from(Fields.field("_id", "year"))
                ).max("rate").as("rate")
        ),
        CONNECTION_DOC_NAME,
        Document.class
)
```
2. 연도별 조회한 접속율을 가진 단말기 목록을 검색한다.
```java
this.mongoTemplate.aggregate(
        newAggregation(
                match(/* 1번의 응답 내용 */),
                project(
                        "year", "device_id", "rate"
                ),
                sort(
                        Sort.by(cond == Condition.BY_DEVICE ? "device_id" : "year")
                )
        ),
        CONNECTION_DOC_NAME, Connection.class
)
```
3. 두개의 데이터를 조합하여 앞서 언급한 DEVICE 정보를 추가하여 응답 메시지를 구성한다.
```java
connections.getMappedResults().stream().map( m ->
        new DeviceRate( m.getYear(), m.getDevice_id(), getDeviceNameById(m.getDevice_id()), m.getRate())
).collect(Collectors.toList());
```
  
- 특정 년도를 입력받아 그 해에 인터넷뱅킹에 가장 많이 접속하는 기기 이름을 출력하세요.
```
{
    “result” :
    {   “year”: 2011,
        “device_name”: “데스크탑 컴퓨터”,
        “rate”: 95.1
    }
}
```
REST API 를 이용하여 마지막 경로에 년도 값을 받는다. 단지 년도 값이 숫자가 아닐 경우 오류를 리턴한다.
```java
try {
    List list = connectionRateStatService.getMaxRateDeviceByYear(Integer.parseInt(year));
    Map<String, List> result = new HashMap<>();
    result.put("result", list);
    return result;
} catch ( NumberFormatException ex) {
    throw new InvalidYearException();
}
```
파라미터로 받은 연도를 이용하여 몽고디비에 해당 연도의 접속률이 가장 높은 값을 $GROUP 을 이용하여 얻어온다.
```java
AggregationResults<Document> connections = this.mongoTemplate.aggregate(
        newAggregation(
                match(
                        Criteria.where("year").is(year)
                ),
                group().max("rate").as("rate")
        ),
        CONNECTION_DOC_NAME,
        Document.class
)
```
그리고 이전 문제와 마찬가지로 년도와 접속률을 이용하여 단말기ID 를 얻고, 단말기ID의 이름을 얻어 리턴한다.
본 응답에는 result 값이 배열로 표시 되지 않았나 제출된 코드에는 배열로 처리하였다. 이유는 접속률이 동점일 경우를 고려하있다.

- 디바이스 아이디를 입력받아 인터넷뱅킹에 접속 비율이 가장 많은 해를 출력하세요.
```
{
    “result” :
    {   “device_name”: 스마트폰,
        “year”: 2017,
        “rate”: 90.6 }
}
```
REST API 를 이용하여 마지막 경로에 단말기코드 값을 받는다.
파라미터로 받은 단말기 코드를 이용하여 몽고디비에 해당 단말기의 접속률이 가장 높은 값을 $GROUP 을 이용하여 얻어온다.
```java
AggregationResults<Document> connections = this.mongoTemplate.aggregate(
        newAggregation(
                match(
                        Criteria.where("device_id").is(deviceId)
                ),
                group().max("rate").as("rate")
        ),
        CONNECTION_DOC_NAME,
        Document.class
)
```
> 그리고 이전 문제와 마찬가지로 단말기코드와 접속률을 이용하여 연도를 얻고, 단말기ID의 이름을 얻어 리턴한다.
> 본 응답에는 result 값이 배열로 표시 되지 않았나 제출된 코드에는 배열로 처리하였다. 이유는 접속률이 동점일 경우를 고려하있다.

   
#### 옵션 문제
- 인터넷뱅킹 접속 기기 ID 를 입력받아 2019 년도 인터넷뱅킹 접속 비율을 예측하는 API 를 개발하세요.
> 본 문제에 대한 알고리즘은 2차 다항회귀분석 알고리즘을 이용하였으며, 알고리즘 라이브러리는 아래와 같이 오픈 소스를 이용하였다.
```java
implementation 'org.apache.commons:commons-math3:3.6.1'
```
1. 우선 단말기별로 과거 이용률 데이터를 조회한다.
```java
public Double predictRateByDevice(final String deviceId) {
    AggregationResults<Document> doc = mongoTemplate.aggregate(
            newAggregation(
                    match(Criteria.where("device_id").is(deviceId)),
                    sort(Sort.by("year").ascending()),
                    project("year", "rate")
            ),
            CONNECTION_DOC_NAME,
            Document.class
    );
    return calcPredict(doc.getMappedResults());
}
```
 2. 2차 다항식 회귀분석 알고리즘을 이용하여 2019년의 예측치를 얻는다. 이때 요청되는 값은 퍼센트이고 이용률은 모든 사용자가 사용한다고 하여도 100%를 넘어갈 수 없으므로
 최대값은 100 으로 제한한다.
 ```java
private static final Double calcPredict(final List<Document> documents) {
    final WeightedObservedPoints obs = new WeightedObservedPoints();
    // 표본값을 알고리즘에 맞는 포맷으로 변환
    for ( Document doc : documents ) {
        obs.add(doc.getInteger("year"), doc.getDouble("rate"));
    }
    // 알고리즘 세팅(2차 다항식, 최대값은 100
    PolynomialCurveFitter polynomialCurveFitter = PolynomialCurveFitter.create(2).withMaxIterations(100);
    // 표본값 세팅
    PolynomialFunction polynomialFunction = new PolynomialFunction(polynomialCurveFitter.fit(obs.toList()));
    // 2019 년의 예측치 계산
    return polynomialFunction.value(THIS_YEAR);
}
```
## 빌드 및 실행 방법
GIT 으로부터 소스 코드를 내려 받은 후 아래와 갈이 실행할 수 있다. 
```
C:/> git clone https://github.com/KimSeokWon/quiz-stats.git
C:/> mvn build  #빌드
C:/> mvn spring-boot:run #실행
C:/> mvn test #단위 시험 코드
```

