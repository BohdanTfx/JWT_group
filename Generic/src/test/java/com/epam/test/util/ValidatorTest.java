package com.epam.test.util;

import org.junit.Test;

import com.epam.test.util.ValidationParametersBuilder.Parameters;

import junit.framework.TestCase;

public class ValidatorTest extends TestCase {

	@Test
	public void testValidateNumb() throws Exception {
		Parameters parameters = ValidationParametersBuilder.createParameters()
				.pattern("[0-9].*");
		assertTrue(Validator.validate(parameters, "446456", "565"));

		Parameters parameters2 = ValidationParametersBuilder.createParameters()
				.pattern("[0-9]*");
		assertTrue(Validator.validate(parameters2, "", "", ""));
	}

	@Test
	public void testValidateStr() throws Exception {
		Parameters parameters = ValidationParametersBuilder.createParameters()
				.maxLength(10).minLength(5).notEmptyString(true);
		assertFalse(Validator.validate(parameters, "asf", "asfsfsdg"));
		assertFalse(Validator.validate(parameters, "", "dsgdd"));
		assertFalse(Validator.validate(parameters, "adasffsfdfsdfddddddddddd",
				"asfsdgd"));

		assertTrue(Validator.validate(parameters, new String[] {}));
		assertTrue(Validator.validate(parameters, "446456", "sdgdg"));
		assertTrue(Validator.validate(parameters, "1234567890"));
	}

	@Test
	public void testValidateEmail() throws Exception {
		Parameters parameters = ValidationParametersBuilder
				.createParameters()
				.pattern(
						"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]+)")
				.maxLength(50).minLength(5);

		assertTrue(Validator.validate(parameters, "bsdfsdgsaf@ssdfsdg.asfsdf",
				"bsdfsdgsaf@ssdfsdg.asfsdf"));
		assertFalse(Validator.validate(parameters, "bsdfsdgsaf@ssdfsdg.asfsdf",
				"bsdfsdgsadfgfsdf"));
		assertFalse(Validator.validate(parameters, "sdfdsadsafsf"));
	}
}
