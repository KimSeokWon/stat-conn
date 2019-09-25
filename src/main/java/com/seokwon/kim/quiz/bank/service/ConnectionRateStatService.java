package com.seokwon.kim.quiz.bank.service;

import com.seokwon.kim.quiz.bank.exception.InvalidYearException;
import com.seokwon.kim.quiz.bank.exception.NotFoundDeviceException;
import com.seokwon.kim.quiz.bank.model.Connection;
import com.seokwon.kim.quiz.bank.model.Device;
import com.seokwon.kim.quiz.bank.model.DeviceRate;
import com.seokwon.kim.quiz.bank.repository.ConnectionRepository;
import com.seokwon.kim.quiz.bank.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service @Slf4j
public class ConnectionRateStatService {

    private final static String CONNECTION_DOC_NAME = "CONNECTION";
    private final static String DEVICE_DOC_NAME = "DEVICE";

    private final DeviceRepository deviceRepository;
    private final ConnectionRepository connectionRepository;

    private final MongoTemplate   mongoTemplate;

    private Map<String, String> devices = new HashMap<>();

    /**
     * 연도별 최대 접속한 단말기 컬렉션
     */
    private final static String COLLECTION_DEVICE_BY_YEAR_MAX_RATE = "DEVICE_BY_YEAR_MAX_RATE";
    /**
     * 단말기별 최대 접속한 년도 컬렉션
     */
    private final static String COLLECTION_YEAR_BY_DEVICE_MAX_RATE = "YEAR_BY_DEVICE_MAX_RATE";

    enum Condition{
        BY_YEAR,
        BY_DEVICE
    }

    @Autowired
    public ConnectionRateStatService(final MongoTemplate   mongoTemplate,
                                     final DeviceRepository deviceRepository,
                                     final ConnectionRepository connectionRepository) {
        this.mongoTemplate = mongoTemplate;
        this.deviceRepository = deviceRepository;
        this.connectionRepository = connectionRepository;
    }


    @PostConstruct
    private void setDeviceInfo() {
        List<Device> list = deviceRepository.findAll();
        for ( Device d : list ) {
            devices.put(d.getDevice_id(), d.getDevice_name());
        }
    }

    /**
     * 연도별 최대 접속 단말기 정보 조회
     * @return
     */
    public List<DeviceRate> getMaxRateAnnually() {

        AggregationResults<Document> obj = this.mongoTemplate.aggregate(
                newAggregation(
                        group(
                                Fields.from(Fields.field("_id", "year"))
                        ).max("rate").as("rate")
                ),
                CONNECTION_DOC_NAME,
                Document.class
        );
        return convertResponse(getMaxRateDevice(obj, Condition.BY_YEAR));
    }

    /**
     * 데이터베이스에서 가져온 결과 목록을 응답 객체로 변환한다.
     * @param connections
     * @return 변환된 응답 객체
     */
    private final List<DeviceRate> convertResponse(AggregationResults<Connection> connections) {
        return connections.getMappedResults().stream().map( m ->
                new DeviceRate( m.getYear(), m.getDevice_id(), getDeviceNameById(m.getDevice_id()), m.getRate())
        ).collect(Collectors.toList());
    }

    private final AggregationResults<Connection> getMaxRateDevice(AggregationResults<Document> connections, Condition cond) {
        List<Criteria> list = new ArrayList();
        Criteria criteria = new Criteria();
        if ( cond == Condition.BY_YEAR ) {
            for ( Document conn : connections.getMappedResults() ) {
                list.add(
                        Criteria.where("year").is(conn.get("_id")).and("rate").is(conn.get("rate"))
                );
            }
        } else {
            for ( Document conn : connections.getMappedResults() ) {
                list.add(
                        Criteria.where("device_id").is(conn.get("_id")).and("rate").is(conn.get("rate"))
                );
            }
        }
        criteria.orOperator(list.toArray(new Criteria[0]));
        return this.mongoTemplate.aggregate(
                newAggregation(
                        match(criteria),
                        project(
                                "year", "device_id", "rate"
                        ),
                        sort(
                                Sort.by(cond == Condition.BY_DEVICE ? "device_id" : "year")
                        )
                ),
                CONNECTION_DOC_NAME, Connection.class
        );
    }

    /**
     * 해당 년도의 최대 접속 단말기 조회
     * @param year
     * @return
     */
    public List<DeviceRate> getMaxRateDeviceByYear(Integer year) {
        AggregationResults<Document> connections = this.mongoTemplate.aggregate(
                newAggregation(
                        match(
                                Criteria.where("year").is(year)
                        ),
                        group().max("rate").as("rate")
                ),
                CONNECTION_DOC_NAME,
                Document.class
        );

        List<Criteria> criteriaList = createCriteriaForByDevice(connections, year, Condition.BY_YEAR);
        Criteria criteria = new Criteria();
        criteria.orOperator(criteriaList.toArray(new Criteria[0]));
        return this.mongoTemplate.aggregate(
                newAggregation(
                        match(criteria),
                        project(
                                "year", "device_id", "rate"
                        ),
                        sort(
                                Sort.by("device_id")
                        )
                ),
                CONNECTION_DOC_NAME, Connection.class
        ).getMappedResults().stream().map( m ->
                new DeviceRate( m.getYear(), m.getDevice_id(), getDeviceNameById(m.getDevice_id()), m.getRate())
        ).collect(Collectors.toList());
    }


    /**
     * 접속기기의 최대 접속률 연도 조회
     * @param deviceId
     * @return
     */
    public List<DeviceRate> getMaxRateYearByDevice(String deviceId)  {
        Instant instant = Instant.now();
        AggregationResults<Document> connections = this.mongoTemplate.aggregate(
                newAggregation(
                        match(
                                Criteria.where("device_id").is(deviceId)
                        ),
                        group().max("rate").as("rate")
                ),
                CONNECTION_DOC_NAME,
                Document.class
        );

        List<Criteria> criteriaList = createCriteriaForByDevice(connections, deviceId, Condition.BY_DEVICE);
        Criteria criteria = new Criteria();
        criteria.orOperator(criteriaList.toArray(new Criteria[0]));
        log.debug("Elapsed time: {}ms", Duration.between(instant, Instant.now()).toMillis());
        return this.mongoTemplate.aggregate(
                newAggregation(
                        match(criteria),
                        project(
                                "year", "device_id", "rate"
                        ),
                        sort(
                                Sort.by("year")
                        )
                ),
                CONNECTION_DOC_NAME, Connection.class
        ).getMappedResults().stream().map( m ->
                new DeviceRate( m.getYear(), m.getDevice_id(), getDeviceNameById(m.getDevice_id()), m.getRate())
        ).collect(Collectors.toList());
    }

    private List<Criteria> createCriteriaForByDevice(
            AggregationResults<Document> basicConditions,
            Object target,
            Condition cond) throws NotFoundDeviceException {
        List<Criteria> list = new ArrayList();
        if ( basicConditions.getMappedResults().isEmpty() ) {
            if ( Condition.BY_DEVICE == cond ) {
                throw new NotFoundDeviceException();
            } else {
                throw new InvalidYearException();
            }
        }
        for ( Document conn : basicConditions.getMappedResults() ) {
            list.add(
                    cond == Condition.BY_DEVICE ?
                     Criteria.where("device_id").is(target).and("rate").is(conn.get("rate")) :
                            Criteria.where("year").is(target).and("rate").is(conn.get("rate"))
            );
        }
        return list;
    }

    public final String getDeviceNameById(String deviceId) {
        if ( devices.isEmpty() ) {
            setDeviceInfo();
        }
        if ( devices.containsKey(deviceId) == false ) {
            throw new NotFoundDeviceException();
        }
        return devices.get(deviceId);
    }

//
//    void createTempCollections() {
//        this.mongoTemplate.aggregate(
//                newAggregation(
//                        group("year")
//                        .max("rate").as("rate"),
//                        out("MAX_RATE_ANNUALLY")
//                ).withOptions(newAggregationOptions().cursorBatchSize(1024*10).allowDiskUse(false).build()),
//                CONNECTION_DOC_NAME,
//                BasicDBObject.class
//        );
//        this.mongoTemplate.aggregate(
//                newAggregation(
//                        group("device_id")
//                                .max("rate").as("rate"),
//                        out("MAX_RATE_BY_DEVICE")
//                ).withOptions(newAggregationOptions().cursorBatchSize(1024*10).allowDiskUse(false).build()),
//                CONNECTION_DOC_NAME,
//                BasicDBObject.class
//        );
//        createYearByDeviceNRate();
//        createDeviceByYearNRate();
//    }
//
//
//
//    /**
//     * 단말기별 최대 접속량을 가진 연도를 조회하는 임시컬렉션을 생성한다.
//     */
//    private void createYearByDeviceNRate() {
//        this.mongoTemplate.aggregate(
//                newAggregation(
//                        lookup("MAX_RATE_BY_DEVICE", "device_id", "_id", "max_rate_info"),
//                        lookup("MAX_RATE_BY_DEVICE", "rate", "rate", "max_rate_info"),
//                        project(
//                                Fields.from(
//                                        Fields.field("year"),
//                                        Fields.field("rate"),
//                                        Fields.field("device_id"),
//                                        Fields.field("max_rate_info")
//                                )
//
//                        ).and("max_rate_info").size().as("size"),
//                        match(
//                             Criteria.where("size").gt(0)
//                        ),
//                        project(
//                                "year", "rate", "device_id"
//                        ),
//                        out(COLLECTION_YEAR_BY_DEVICE_MAX_RATE)
//                ).withOptions(newAggregationOptions().cursorBatchSize(1024*10).allowDiskUse(false).build()),
//                CONNECTION_DOC_NAME,
//                BasicDBObject.class
//        );
//    }
//
//    /**
//     * 연별 최대 접속량을 가진 단말기를 조회하는 임시컬렉션을 생성한다.
//     */
//    private void createDeviceByYearNRate() {
//        this.mongoTemplate.aggregate(
//                newAggregation(
//                        lookup("MAX_RATE_ANNUALLY", "year", "_id", "max_rate_info"),
//                        lookup("MAX_RATE_ANNUALLY", "rate", "rate", "max_rate_info"),
//                        project(
//                                Fields.from(
//                                        Fields.field("year"),
//                                        Fields.field("rate"),
//                                        Fields.field("device_id"),
//                                        Fields.field("max_rate_info")
//                                )
//
//                        ).and("max_rate_info").size().as("size"),
//                        match(
//                                Criteria.where("size").gt(0)
//                        ),
//                        project(
//                                "year", "rate", "device_id"
//                        ),
//                        out(COLLECTION_DEVICE_BY_YEAR_MAX_RATE)
//                ).withOptions(newAggregationOptions().cursorBatchSize(1024*10).allowDiskUse(false).build()),
//                CONNECTION_DOC_NAME,
//                BasicDBObject.class
//        );
//    }
}
