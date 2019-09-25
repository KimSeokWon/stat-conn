package com.seokwon.kim.quiz.bank.service;

import com.seokwon.kim.quiz.bank.model.DeviceRate;
import org.apache.commons.lang3.StringUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import static org.junit.Assert.*;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConnectionRateStatServiceTest {


    @Autowired
    private ConnectionRateStatService connectionRateStatService;


    @Test
    public void successToGetMaxRateDeviceAnnually() {
        List<DeviceRate> conns = connectionRateStatService.getMaxRateAnnually();

        assertTrue(conns != null);
        assertTrue(conns.get(0).getYear() == 2011);
        assertTrue(conns.get(0).getRate() == 95.1);
        assertTrue(StringUtils.equals(conns.get(0).getDevice_name(), "데스크탑 컴퓨터"));

        assertTrue(conns.get(1).getYear() == 2012);
        assertTrue(conns.get(1).getRate() == 93.9);
        assertTrue(StringUtils.equals(conns.get(1).getDevice_name(), "데스크탑 컴퓨터"));

        assertTrue(conns.get(2).getYear() == 2013);
        assertTrue(conns.get(2).getRate() == 67.1);
        assertTrue(StringUtils.equals(conns.get(2).getDevice_name(), "데스크탑 컴퓨터"));

        assertTrue(conns.get(3).getYear() == 2014);
        assertTrue(conns.get(3).getRate() == 64.2);
        assertTrue(StringUtils.equals(conns.get(3).getDevice_name(), "스마트폰"));

        assertTrue(conns.get(4).getYear() == 2015);
        assertTrue(conns.get(4).getRate() == 73.2);
        assertTrue(StringUtils.equals(conns.get(4).getDevice_name(), "스마트폰"));
    }

    @Test
    public void successToGetMaxRateYearByDevice() {
        List<DeviceRate> list = connectionRateStatService.getMaxRateYearByDevice("DEVICE_00004");
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).getRate() == 3.3);
        assertTrue(StringUtils.equals(list.get(0).getDevice_name(), "스마트패드"));

        list = connectionRateStatService.getMaxRateYearByDevice("DEVICE_00000");
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).getRate() == 90.6);
        assertTrue(list.get(0).getYear() == 2017);
        assertTrue(StringUtils.equals(list.get(0).getDevice_name(), "스마트폰"));
    }
    @Test
    public void successToGetMaxRateDeviceByYear() {
        List<DeviceRate> list = connectionRateStatService.getMaxRateDeviceByYear(2018);
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).getRate() == 90.5);
        assertTrue(StringUtils.equals(list.get(0).getDevice_name(), "스마트폰"));

        list = connectionRateStatService.getMaxRateDeviceByYear(2017);
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).getRate() == 90.6);
        assertTrue(StringUtils.equals(list.get(0).getDevice_name(), "스마트폰"));
    }
}
