package com.it.reggie.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

/**
 * SMS sending utility class
 */
public class SMSUtils {

	/**
	 * Send SMS
	 * @param signName Signature
	 * @param templateCode Template
	 * @param phoneNumbers Phone number
	 * @param param Parameter
	 */
	public static void sendMessage(String signName, String templateCode, String phoneNumbers, String param) {
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "", "");
		IAcsClient client = new DefaultAcsClient(profile);

		SendSmsRequest request = new SendSmsRequest();
		request.setSysRegionId("cn-hangzhou");
		request.setPhoneNumbers(phoneNumbers);
		request.setSignName(signName);
		request.setTemplateCode(templateCode);
		request.setTemplateParam("{\"code\":\"" + param + "\"}");
		try {
			SendSmsResponse response = client.getAcsResponse(request);
			System.out.println("SMS sent successfully");
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
}
