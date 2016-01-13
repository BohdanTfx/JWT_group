package com.epam.test.util;

import com.epam.test.util.ValidationParametersBuilder.Parameters;

public abstract class Validator {
	public static boolean validate(Parameters validationParam,
			String... strings)
	{
		if (validationParam.getNotEmptyString() != null)
			if (validationParam.getNotEmptyString()) {
				for (String string : strings)
					if (string == null || string.isEmpty())
						return false;
			} else
				for (String string : strings)
					if (string != null && !string.isEmpty())
						return false;

		if (strings == null)
			return false;

		if (validationParam.getMaxLength() != null)
			for (String string : strings)
				if (string == null
						|| validationParam.getMaxLength() < string.length())
					return false;
		if (validationParam.getMinLength() != null)
			for (String string : strings)
				if (string == null
						|| validationParam.getMinLength() > string.length())
					return false;
		if (validationParam.getPattern() != null)
			for (String string : strings)
				if (string == null
						|| !string.matches(validationParam.getPattern()))
					return false;

		return true;
	}
}
