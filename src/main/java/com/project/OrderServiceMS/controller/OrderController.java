package com.project.OrderServiceMS.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.client.RestTemplate;

import com.project.OrderServiceMS.DTO.OrderDetailsDTO;
import com.project.OrderServiceMS.DTO.ProductId;
import com.project.OrderServiceMS.DTO.ProductsOrderedDTO;
import com.project.OrderServiceMS.entity.OrderDetailsEntity;
import com.project.OrderServiceMS.entity.ProductsOrderedEntity;
import com.project.OrderServiceMS.service.OrderService;

@RestController
public class OrderController {
	
	@Autowired
	private OrderService orderservice;
	@Autowired
	public RestTemplate restTemplate;
	@Value("${userServiceUrl}")
	public String cartServiceUrl;
	@Value("${productServiceUrl}")
	public String productServiceUrl;
	
	
	@GetMapping(value="/orders/{buyerId}", produces= MediaType.APPLICATION_JSON_VALUE)
	public ArrayList <OrderDetailsDTO> getAllOrders(@PathVariable("buyerId") Integer buyerId ) {
		return orderservice.getAllOrders(buyerId);	
	}
	
	@DeleteMapping(value="/orders/delete/{orderId}", consumes= MediaType.APPLICATION_JSON_VALUE)
	public String deleteOrder(@PathVariable("orderId") Integer orderId ) {
		return orderservice.deleteOrder(orderId);
	}
	 @PutMapping(value="/{orderId}/{prodId}/status")
	  public String updateStatus (@RequestBody ProductsOrderedDTO product) { 
	  Integer orderId=product.getOrderId(); 
	  Integer prodId=product.getProdId(); 
	  String status= product.getStatus(); 
	  return orderservice.updateStatus(orderId,prodId,status); }
	
}