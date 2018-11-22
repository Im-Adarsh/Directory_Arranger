package com.arranger.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.JRootPane;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arranger.UI.Validator;

class ValidatorTest {

	JRootPane rootPane;
	Validator validator;

	@BeforeEach
	void setUp() throws Exception {
		validator = new Validator(rootPane);
	}

	@Test
	void testDir() {
		assertEquals(false, validator.isNotValidInput("c:\\test"));
		assertEquals(false, validator.isNotValidInput("c:\\test\\test space"));
		assertEquals(false, validator.isNotValidInput("c:\\test\\test-hyphen"));
	}

	@Test
	void testDummyPath() {
		assertEquals(false, validator.isNotValidInput("c:\\parentdi.r90\\child1\\child2"));
		assertEquals(false, validator.isNotValidInput("c:\\test\\chil0.1\\child2\\child3\\jj\\bh"));
		assertEquals(true, validator.isNotValidInput("c:/test/child1/"));
		assertEquals(true, validator.isNotValidInput("c://test//child/"));
		assertEquals(false, validator.isNotValidInput("c:\\\\test"));
		assertEquals(false, validator.isNotValidInput("c:\\test\\chil0.1\\child2\\child3\\jj\\jht\\k"));
	}
}
