package com.project.OrderServiceMS.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.OrderServiceMS.DTO.OrderDetailsDTO;
import com.project.OrderServiceMS.DTO.ProductsOrderedDTO;
import com.project.OrderServiceMS.controller.OrderController;
import com.project.OrderServiceMS.entity.OrderDetailsEntity;
import com.project.OrderServiceMS.entity.ProductsOrderedEntity;
import com.project.OrderServiceMS.repository.OrderDetailsRepository;
import com.project.OrderServiceMS.repository.ProductsOrderedRepository;

@Service
public class OrderService {
	@Autowired
	private OrderDetailsRepository orderrepo;
	@Autowired
	private OrderDetailsEntity orderEntity;
	@Autowired
	private ProductsOrderedRepository orderProdsRepo;
	@Autowired
	private ProductsOrderedEntity productsOrdered;
	@Autowired
	public RestTemplate restTemplate;
	@Value("${userServiceUrl}")
	public String userServiceUrl;


		
		// This method is to view orders placed on products.(by buyerId)
		public ArrayList<OrderDetailsDTO> getAllOrders(Integer Buyerid){
			List<ProductsOrderedEntity> product = (List<ProductsOrderedEntity>) orderProdsRepo.findAll();
			ArrayList<OrderDetailsDTO> orders =new ArrayList<>();
			List<OrderDetailsEntity> ord= (List<OrderDetailsEntity>) orderrepo.findAll();
			for(OrderDetailsEntity ord1:ord) {
				if(ord1.getBuyerId().equals(Buyerid)) {
					ArrayList<ProductsOrderedDTO> orderedProducts =new ArrayList<>();
					for(ProductsOrderedEntity orderprod: product) {
						if(ord1.getOrderId().equals(orderprod.getOrderId())) {
							ProductsOrderedDTO prod =new ProductsOrderedDTO();
							prod.setOrderId(orderprod.getOrderId());
							prod.setPrice(orderprod.getPrice());
							prod.setProdId(orderprod.getProdId());
							prod.setQuantity(orderprod.getQuantity());
							prod.setSellerid(orderprod.getSellerid());
							prod.setStatus(orderprod.getStatus());
							orderedProducts.add(prod);
						}
						OrderDetailsDTO od= new OrderDetailsDTO();
						od.setAddress(ord1.getAddress());
						od.setAmount(ord1.getAmount());
						od.setBuyerId(ord1.getBuyerId()); 
						od.setDate(ord1.getDate());
						od.setOrderId(ord1.getOrderId());
						od.setStatus(ord1.getStatus());
						od.setOrderedProducts(orderedProducts);
						orders.add(od);
					}
				}
			}
			return orders;
		}
		
		public String deleteOrder(Integer orderId) {
			List<OrderDetailsEntity> ordersEntities=(List<OrderDetailsEntity>) orderrepo.findAll();
				// Deleting the order
			Integer sizeorder=ordersEntities.size();
				for(OrderDetailsEntity order: ordersEntities){
					if(order.getOrderId().equals(orderId)) {
						orderrepo.delete(order);					
					}
				}
				List<ProductsOrderedEntity> products=(List<ProductsOrderedEntity>) orderProdsRepo.findAll();
				Integer sizeproduct=products.size();
				// Deleting all the product ordered in that order
				for(ProductsOrderedEntity product:products) {
					if(product.getOrderId().equals(orderId)) {
							orderProdsRepo.delete(product);
					}}
				List<OrderDetailsEntity> orders=(List<OrderDetailsEntity>) orderrepo.findAll();
				List<ProductsOrderedEntity> prod=(List<ProductsOrderedEntity>) orderProdsRepo.findAll();
				
			if(sizeorder>orders.size()& sizeproduct>prod.size()){
				return "Order "+ orderId+ " is deleted successfully from the records";
			}else{
				return "Deletion cannot be done as the order with orderId "+orderId+ " does not exist";
			}
	}
	
	
		
		public Integer[] usingRewardPoints(Integer buyerId, int eligibleDiscount) {
	        String getrewardUrl=userServiceUrl+"rewardPoint/"+buyerId;
	        
	        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(getrewardUrl, Integer.class);
	       
	        Integer reward=responseEntity.getBody();
	        Integer discount=reward/4;
	        if(discount>eligibleDiscount) {
	            discount=eligibleDiscount;
	            reward=reward-eligibleDiscount*4;
	        }else {
	            reward=0;
	        }
	        Integer [] valuesArray=new Integer[2];
	        valuesArray[0]=discount;valuesArray[1]=reward;
	        return valuesArray;
		}
			
		
	
	 
	
	  public String updateStatus(Integer orderId,Integer prodId,String status) {
	  Boolean flag=false; 
	  try {
	  List<ProductsOrderedEntity> products=(List<ProductsOrderedEntity>) orderProdsRepo.findAll();
	 for(int i=0;i<products.size();i++){ 
	 ProductsOrderedEntity product=products.get(i); //Checking the orderId and ProdId before updating
	  if(product.getOrderId().equals(orderId) && product.getProdId().equals(prodId)) {
	  BeanUtils.copyProperties(product,productsOrdered);
	  orderProdsRepo.delete(product); productsOrdered.setStatus(status);
	  orderProdsRepo.save(productsOrdered); 
	  flag=true; } } 
	 }catch(Exception e){
	  e.printStackTrace(); 
	  return "Error in updating the order! Contact your Admin"; } 
	  if(flag){
	return  "Order status updated successfully"; }
	  else{ 
	return "Updation is not successful. Check for issues"; } }
	  
	 
	  }
	 
