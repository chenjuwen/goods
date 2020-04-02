package com.heasy.goods.http;

import okhttp3.Response;

public interface HttpClientListener {
	void onReponse(Response response);
}
