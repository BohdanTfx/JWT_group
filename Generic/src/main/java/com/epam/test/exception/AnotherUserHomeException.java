package com.epam.test.exception;

public class AnotherUserHomeException extends Exception {
	public AnotherUserHomeException() {
		super();
	}

	public AnotherUserHomeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AnotherUserHomeException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnotherUserHomeException(String message) {
		super(message);
	}

	public AnotherUserHomeException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = -3929124773936741106L;

}
