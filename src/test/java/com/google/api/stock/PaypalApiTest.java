package com.google.api.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PaypalApiTest {
	private static  String url = 
			"http://finance.google.com/finance/info?client=ig&q=NSE:{stock_name}";
	
	@DataProvider(name="listofStockSybols")
	public Object[][] getStockSymboldDP(){
		//for exercises hard coded stocks
		// we can keep a spread sheet
		String[][] stockSymbols = {{"PYPL"}, {"PP"}, {"EBAY"}};
		return stockSymbols;
	}
	
	@Test(dataProvider="listofStockSybols")
	public void test1(String stockName) throws Exception{
		String updatedUrl = url.replace("{stock_name}", stockName);
		System.out.println(updatedUrl);
		// Send the request and response
		HttpResponse response = sendandReceiveGetRequest(updatedUrl);
		
		// check basic validations
		int statusCode = response.getStatusLine().getStatusCode();
		Assert.assertEquals(statusCode, 200);
		String statusMessage = response.getStatusLine().getReasonPhrase();
		Assert.assertEquals(statusMessage, "OK");
		
		//verify the data
		String responseXML = getResponseMessage(response);
		System.out.println(responseXML);
		//we can get the data after parsing using Gson or Jackson
		//for exercise purpose get stock price for now.
		//probably we need to keep the value fixed and test it
		Assert.assertTrue(responseXML.contains("\"l\" : \""), "Check stock price tag");
	}

	private HttpResponse sendandReceiveGetRequest(String urlParam) throws IOException, ClientProtocolException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(urlParam);
		HttpResponse response = client.execute(request);
		return response;
	}

	public String getResponseMessage(HttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}
}
