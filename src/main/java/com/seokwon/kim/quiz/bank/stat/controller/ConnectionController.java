package com.seokwon.kim.quiz.bank.stat.controller;

import com.seokwon.kim.quiz.bank.exception.InvalidYearException;
import com.seokwon.kim.quiz.bank.stat.model.DeviceRate;
import com.seokwon.kim.quiz.bank.stat.service.ConnectionRateStatService;
import com.seokwon.kim.quiz.bank.stat.service.PredictConnectionRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableCaching
@RestController @Slf4j
@RequestMapping("/api/device-stat")
public class ConnectionController {

    private final ConnectionRateStatService connectionRateStatService;
    private final PredictConnectionRateService predictConnectionRateService;
    @Autowired
    public ConnectionController(final ConnectionRateStatService connectionRateStatService,
                                final PredictConnectionRateService predictConnectionRateService) {
        this.connectionRateStatService = connectionRateStatService;
        this.predictConnectionRateService = predictConnectionRateService;
    }

    @GetMapping("/by-year")
    public @ResponseBody
    Map<String, List> getMaxRateDeviceAnnually() {
        List list = connectionRateStatService.getMaxRateAnnually();
        Map<String, List> result = new HashMap<>();
        result.put("devices", list);
        return result;
    }

    @GetMapping("/year/{year}")
    public @ResponseBody
    Map<String, List> getMaxRateDeviceByYear(@PathVariable("year") final String year) {

        try {
            List list = connectionRateStatService.getMaxRateDeviceByYear(Integer.parseInt(year));
            Map<String, List> result = new HashMap<>();
            result.put("result", list);
            return result;
        } catch ( NumberFormatException ex) {
            throw new InvalidYearException();
        }
    }

    @GetMapping("/device/{device}")
    public @ResponseBody
    Map<String, List> getMaxRateYearByDevice(@PathVariable("device") final String deviceId) {
        Instant start = Instant.now();
        List list = connectionRateStatService.getMaxRateYearByDevice(deviceId);

        Map<String, List> result = new HashMap<>();
        result.put("result", list);
        log.debug("Elapsed time: {}ms", Duration.between(start, Instant.now()).toMillis());
        return result;
    }

    /**
     * 2019 통계예측
     * @param deviceId 단말기 ID
     * @return 2019년의 단말기 접속율 통계 예측
     */
    @GetMapping("/predict/device/{device}")
    public @ResponseBody
    Map<String, Object> predictRateByDevice(@PathVariable("device") final String deviceId) {
        Map<String, Object> result = new HashMap<>();
        result.put("predict", new DeviceRate(
                2019,
                deviceId,
                connectionRateStatService.getDeviceNameById(deviceId),
                ((double)(Math.round(predictConnectionRateService.predictRateByDevice(deviceId)*10)-5))/10)
        );

        return result;
    }
}
