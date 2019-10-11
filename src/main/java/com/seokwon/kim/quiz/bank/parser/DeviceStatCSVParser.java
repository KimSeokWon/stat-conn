package com.seokwon.kim.quiz.bank.parser;


import com.seokwon.kim.quiz.bank.exception.DeviceStatException;
import com.seokwon.kim.quiz.bank.stat.model.Connection;
import com.seokwon.kim.quiz.bank.stat.model.Device;
import com.seokwon.kim.quiz.bank.stat.repository.ConnectionRepository;
import com.seokwon.kim.quiz.bank.stat.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV 파일을 읽어 들여 파싱한다.
 */
@Component
@Slf4j
public class DeviceStatCSVParser {

    /**
     * 스탬프파일의 확장자를 정의한다.
     */
    private final static String WRITTEN_SUFFIX = ".saved";
    /**
     * 첫번째 컬럼은 년도를 나타낸다.
     */
    private final static int     IDX_YEAR = 0;
    /**
     * 두번째 컬럼은 사용률을 나타낸다.
     */
    private final static int     IDX_USE_RATE = 1;
    /**
     * 세번째 컬럼부터는 단말기를 나타낸다.
     */
    private final static int     START_DEVICE = 2;


    @Autowired
    private ConnectionRepository connectionRepository;
    @Autowired
    private DeviceRepository deviceRepository;

    private List<Device> devices = null;

    @Value("${data.filename}")
    private String filename;
    @Value("${data.disabled}")
    private boolean writtenDisabled;

    /**
     * 기본 파일 로드
     * @throws IOException
     */
    private CSVParser loadCSV() throws IOException {
        return CSVParser.parse(
                new FileInputStream(filename),
                Charset.forName("UTF-8"),
                CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
        );
    }

    /**
     * 헤더 영역에서 디바이스 객체를 생성하여 리턴한다.
     * @return 디바이스 목록을 리턴한다. 디바이스 객체 목록이 없을 경우 비어 있는 List 객체를 리턴한다.
     */
    private List<Device> parseHeaders(final List<String> headers ) {
        final List<Device> devices = new ArrayList<>();
        int startIndex = 0;
        for ( int i = START_DEVICE; i < headers.size() ; i++ ) {
            devices.add(
                    new Device(
                            "DEVICE_"+ StringUtils.leftPad(String.valueOf(startIndex++), 5, "0"),
                            headers.get(i)
                    )
            );
        }
        return devices;
    }

    /**
     * CSV 데이터셋이 저장되지 않았으면 CSV 파일을 읽어 들여 데이터를 저장한다.
     */
    @PostConstruct
    public void save()  throws DeviceStatException {
        if ( !writtenDisabled && isWritten() ) {
            log.info("Dataset have been saved yet then it will be not opened.");
            return;
        }
        clearDatabase();
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

    private void clearDatabase() {
        connectionRepository.deleteAll();
        deviceRepository.deleteAll();
    }
    /**
     * 데이터셋이 저장되면 ${FILE_NAME}.saved 라는 파일을 생성한다.
     * @return 데이터셋이 저장되어 있으면 true 를 리턴한다.
     */
    private boolean isWritten() {
//        File f = new File(filename + WRITTEN_SUFFIX);
//        return f.exists();
        return false;
    }

    /**
     * 파싱이 끝나면 파일 스탬프를 생성한다.
     * @return 파일 생성이 성공하면 true 를 리턴한다.
     * @throws IOException
     */
    private boolean createWrittenFile() throws IOException{
//        if ( writtenDisabled ) return true;
//        File f = new File(filename + WRITTEN_SUFFIX);
//        return f.createNewFile();
        return true;
    }

    /**
     * 헤더를 제외한 바디 영역을 분석하여 객제화 한다.
     * @return
     */
    private List<Connection> parseBody(final List<CSVRecord> csvRecords) throws IOException {
        List<Connection> connections = new ArrayList<>();
        for ( CSVRecord csvRecord : csvRecords ) {
            connections.addAll(parseByRow(csvRecord));
        }
        return connections;
    }

    /**
     * 데이터 한줄을 분석하여 객체화한다.
     * @param record 한줄 레코드
     * @return 객제화한 Connection 을 리턴한다.
     */
    private List<Connection> parseByRow(CSVRecord record) {
        List<Connection> connections = new ArrayList<>();
        devices.forEach( c -> {
            connections.add(
                    new Connection(
                            null,
                            Integer.parseInt(record.get(IDX_YEAR)),
                            Double.parseDouble(record.get(IDX_USE_RATE)),
                            c.getDevice_id(),
                            null,
                            Double.parseDouble(
                                    StringUtils.replace(record.get(c.getDevice_name()), "-", "0")
                            )
                    )
            );
        });
        return connections;
    }

    private final Device getDeviceId(final String deviceName) {
        return devices.stream().filter( c -> StringUtils.equals(deviceName, c.getDevice_name())).findFirst().get();
    }

    /**
     * 파싱이 끝나면 명시적으로 호출해야 한다.
     * @throws IOException
     */
    private void close(final CSVParser csvParser) throws IOException {
        if ( csvParser != null && false == csvParser.isClosed() ) {
            csvParser.close();
        }
    }
}
