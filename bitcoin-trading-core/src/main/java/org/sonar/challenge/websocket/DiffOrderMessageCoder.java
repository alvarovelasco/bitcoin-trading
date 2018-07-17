package org.sonar.challenge.websocket;

import org.sonar.challenge.book.net.json.DiffOrderDecoder;

import com.google.gson.Gson;

public class DiffOrderMessageCoder implements MessageCoder<DiffOrderDecoder> {

	@Override
	public DiffOrderDecoder code(String message) {
		return new Gson().fromJson(message, getObjClass());
	}

	@Override
	public Class<DiffOrderDecoder> getObjClass() {
		return DiffOrderDecoder.class;
	}

}
