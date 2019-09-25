package com.seokwon.kim.quiz.bank.parser;

import com.seokwon.kim.quiz.bank.stat.repository.ConnectionRepository;
import com.seokwon.kim.quiz.bank.stat.repository.DeviceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatDeviceParserTests {

	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private ConnectionRepository connectionRepository;

	@Test
	public void successToSaveDevice() {
		assertTrue(
				deviceRepository.count() > 0
		);
	}
	@Test
	public void successToSaveConnectionStat() {
		assertTrue(
				connectionRepository.count() > 0
		);
	}
}
