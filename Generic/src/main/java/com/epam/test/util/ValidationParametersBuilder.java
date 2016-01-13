package com.epam.test.util;

public abstract class ValidationParametersBuilder {

	public static Parameters createParameters() {
		return new Parameters();
	}

	public static class Parameters {
		private Integer maxLength;
		private Integer minLength;
		private String pattern;
		private Boolean notEmptyString;

		private Parameters() {
		}

		public ValidationParametersBuilder.Parameters maxLength(int maxLength) {
			this.maxLength = maxLength;
			return this;
		}

		public ValidationParametersBuilder.Parameters minLength(int minLength) {
			this.minLength = minLength;
			return this;
		}

		public ValidationParametersBuilder.Parameters pattern(String pattern) {
			this.pattern = pattern;
			return this;
		}

		public ValidationParametersBuilder.Parameters notEmptyString(
				boolean notEmptyString)
		{
			this.notEmptyString = notEmptyString;
			return this;
		}

		public Integer getMaxLength() {
			return maxLength;
		}

		public Integer getMinLength() {
			return minLength;
		}

		public String getPattern() {
			return pattern;
		}

		public Boolean getNotEmptyString() {
			return notEmptyString;
		}
	}
}
