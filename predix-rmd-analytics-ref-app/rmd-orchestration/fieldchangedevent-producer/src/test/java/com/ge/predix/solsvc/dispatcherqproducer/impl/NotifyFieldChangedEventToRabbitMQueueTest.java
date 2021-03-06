/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.dispatcherqproducer.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import com.ge.predix.entity.eventasset.Asset;
import com.ge.predix.entity.eventasset.AssetList;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.fieldchanged.FieldChanged;
import com.ge.predix.entity.fieldchanged.FieldChangedList;
import com.ge.predix.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.predix.entity.fieldidentifiervalue.FieldIdentifierValueList;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.entity.util.map.Entry;
import com.ge.predix.event.fieldchanged.FieldChangedEvent;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 
 * @author 212367843
 */
public class NotifyFieldChangedEventToRabbitMQueueTest {

	@InjectMocks
	private NotifyFieldChangedEventToRabbitMQueue notifyToQ;
	
	@Mock
	private RabbitTemplate fieldChangedEventTemplate;
	
	@Mock
	private MessageConverter messageConverter;
	
	/**
	 * @throws java.lang.Exception
	 *             -
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// setUp before run
	}

	/**
	 * @throws java.lang.Exception
	 *             -
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// clean up
	}

	/**
	 * @throws java.lang.Exception
	 *             -
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * @throws java.lang.Exception
	 *             -
	 */
	@After
	public void tearDown() throws Exception {
		// clean up
	}

	/**
	 * -
	 * @throws Exception -
	 */
	@Test
	public void testNotifyFieldChangedEvent() throws Exception {
		ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
		Connection mockConnection = mock(Connection.class);
		Channel mockChannel = mock(Channel.class);

		when(mockConnectionFactory.newConnection((ExecutorService) null)).thenReturn(mockConnection);
		when(mockConnection.isOpen()).thenReturn(true);
		when(mockConnection.createChannel()).thenReturn(mockChannel);

		when(mockChannel.isOpen()).thenReturn(true);


		this.fieldChangedEventTemplate = new RabbitTemplate(new CachingConnectionFactory(mockConnectionFactory)); 

		FieldChangedEvent data = createFieldChangedEvent(81L, "Reported Distance", "", parseDate("2012-09-11T07:16:13"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				null, null);

		this.notifyToQ.notify(data);

	}

	private FieldChangedEvent createFieldChangedEvent(long triggerField, String triggerFieldName, String assetId,
			Date time, Object externalMapKey, Object externalMapValue) {
		FieldChangedEvent fieldChangedEvent = new FieldChangedEvent();

		FieldChanged fieldChanged = new FieldChanged();
		AttributeMap attributeMap = new AttributeMap();
		Entry entry = new Entry();
		entry.setKey(externalMapKey);
		entry.setValue(externalMapValue);
		attributeMap.getEntry().add(entry);
		fieldChanged.setExternalAttributeMap(attributeMap);

		fieldChanged.setTimeChanged(getXMLDate(time));

		FieldIdentifierValueList fieldIdentifierValueList = new FieldIdentifierValueList();
		List<FieldIdentifierValue> fieldIdentifierValues = new ArrayList<FieldIdentifierValue>();

		FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
		FieldIdentifier fieldIdentifier = new FieldIdentifier();
		// fieldIdentifier.setId(7l); fieldIdentifier.setName("sensorHeight");
		// //Tank Volume Orchestration

		fieldIdentifier.setId(triggerField);
		fieldIdentifier.setName(triggerFieldName); // Temperature Converted Flux
													// Orchestration
		fieldIdentifierValue.setFieldIdentifier(fieldIdentifier);
		fieldIdentifierValues.add(fieldIdentifierValue);
		fieldIdentifierValueList.setFieldIdentifierValue(fieldIdentifierValues);
		fieldChanged.setFieldIdentifierValueList(fieldIdentifierValueList);

		AssetList assetList = new AssetList();
		Asset asset = new Asset();	

		FieldIdentifier assetFieldIdentifier = new FieldIdentifier();
		assetFieldIdentifier.setId(5);
		assetFieldIdentifier.setName("assetId"); //$NON-NLS-1$
		asset.setAssetIdFieldIdentifier(assetFieldIdentifier);

		// FieldMap fieldMap = new FieldMap();

		// fieldMap.getFieldEntry().add(
		// createFieldEntry(1, "customer", "EBMUD", 1));
		// fieldMap.getFieldEntry().add(
		// createFieldEntry(2, "site", "San Ramon", 2));
		// fieldMap.getFieldEntry().add(createFieldEntry(3, "line", "CMS", 3));
		// fieldMap.getFieldEntry().add(
		// createFieldEntry(4, "assetType", "water tank", 4));
		// fieldMap.getFieldEntry()
		// .add(createFieldEntry(5, "assetId", "12099", 5));

		org.mimosa.osacbmv3_3.Asset osaAsset = new org.mimosa.osacbmv3_3.Asset();
		// osaAsset.setSerialNo("12099"); //water tank - tank volume
		// orchestration

		osaAsset.setSerialNo(assetId); // water train - temperature converted
										// flux orchestration
		// asset.setOsaAsset(osaAsset);

		assetList.getAsset().add(asset);

		fieldChanged.setAssetList(assetList);

		// com.ge.dsp.schemas.pm.entity.util.map.Map attributeMap = new
		// com.ge.dsp.schemas.pm.entity.util.map.Map();
		// Entry entry = new Entry();
		// entry.setKey(1);
		// entry.setValue(1);
		// attributeMap.getEntry().add(entry);
		// fieldChanged.setExternalAttributeMap(attributeMap);

		List<FieldChanged> lstFieldChanged = new ArrayList<FieldChanged>();
		lstFieldChanged.add(fieldChanged);

		FieldChangedList fieldChangedList = new FieldChangedList();
		fieldChangedList.getFieldChanged().addAll(lstFieldChanged);
		fieldChangedEvent.setFieldChangedList(fieldChangedList);
		return fieldChangedEvent;
	}

	private static XMLGregorianCalendar getXMLDate(Date dateTime) {
		DatatypeFactory f = null;
		try {
			f = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException("convert to runtime exception", e); //$NON-NLS-1$
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
		cal.setTime(dateTime);
		XMLGregorianCalendar xmLGregorianCalendar = f.newXMLGregorianCalendar(cal);
		return xmLGregorianCalendar;
	}

	private static Date parseDate(String gmtTimeString) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //$NON-NLS-1$
			df.setTimeZone(java.util.TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
			Date date = df.parse(gmtTimeString);
			return date;
		} catch (ParseException e) {
			throw new RuntimeException("convrt to runtime exception", e); //$NON-NLS-1$
		}
	}

}
