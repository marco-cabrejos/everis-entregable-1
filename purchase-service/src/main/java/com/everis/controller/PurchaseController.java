package com.everis.controller;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everis.dto.Response;
import com.everis.model.Customer;
import com.everis.model.Product;
import com.everis.model.Purchase;
import com.everis.service.ICustomerService;
import com.everis.service.IProductService;
import com.everis.service.IPurchaseService;
import com.everis.topic.producer.PurchaseProducer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

	@Autowired
	private IPurchaseService service;
	@Autowired
	private ICustomerService customerService;
	@Autowired
	private IProductService productService;
	
	@Autowired
	private PurchaseProducer producer;
		
	@GetMapping
	public Mono<ResponseEntity<List<Purchase>>> findAll(){ 
		
		return service.findAll()
				.collectList()
				.flatMap(list -> {
					return list.size() > 0 ? 
							Mono.just(ResponseEntity
								.ok()
								.contentType(MediaType.APPLICATION_JSON)
								.body(list)) :
							Mono.just(ResponseEntity
								.noContent()
								.build());
				});
				
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Purchase>> findById(@PathVariable("id") String id){
		return service.findById(id)
				.map(objectFound -> ResponseEntity
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(objectFound))
				.defaultIfEmpty(ResponseEntity
						.noContent()
						.build());
	}
		
	@PostMapping
	public Mono<ResponseEntity<Response>> create(@Valid @RequestBody Purchase purchase, final ServerHttpRequest request){
		Mono<Purchase> monoPurchase = Mono.just(purchase.toBuilder().build());
		Mono<Product> monoProduct = productService.findById(purchase.getProduct().getId()).defaultIfEmpty(Product.builder().build());
		Mono<List<Customer>> monoListCust = Flux.fromIterable(purchase.getCustomerOwner())
				.flatMap(p1->customerService.findByIdentityNumber(p1.getIdentityNumber()))
				.collectList();
		return monoPurchase
		.zipWith(monoProduct,(p,b)->{
			p.setProduct(b);
			return p;
		})
		.zipWith(monoListCust, (p,list)->{
			p.setCustomerOwner(list);
			return p;
		})
		.flatMap(purchasebd->{
			if(purchasebd.getProduct().getId()==null) {
				return Mono.just(ResponseEntity
						.badRequest()
						.body(Response.builder().error("El producto ingresado no existe.").build()));
			}
			if(purchasebd.getCustomerOwner().size()!=purchase.getCustomerOwner().size()) {
				return Mono.just(ResponseEntity
						.badRequest()
						.body(Response.builder().error("El(los) cliente(s) ingresado(s) no existe.").build()));
			}
			long quantityOwners = purchasebd.getCustomerOwner().size();
			long quantityBusinessOwners = purchasebd.getCustomerOwner().stream().filter(c->c.getCustomerType().equals("EMPRESARIAL")).count();
			long quantityPersonalOwners = purchasebd.getCustomerOwner().stream().filter(c->c.getCustomerType().equals("PERSONAL")).count();
			boolean isEmpresarial=false;
			boolean isPersonal=false;
			if(quantityOwners>1) {
				isEmpresarial = quantityBusinessOwners==quantityOwners;
				isPersonal = quantityPersonalOwners==quantityOwners;
				if(quantityBusinessOwners>=1 && quantityPersonalOwners>=1) {
					return Mono.just(ResponseEntity
							.badRequest()
							.body(Response.builder().error("Los titulares deben pertenecer al mismo tipo de cliente. Empresarial o Personal").build()));
				}
				if(isEmpresarial) {
					return Mono.just(ResponseEntity
							.badRequest()
							.body(Response.builder().error("Para cliente empresarial, sólo debe haber como máximo 1 titular.").build()));
				}
			}else if(quantityOwners==0) {
				return Mono.just(ResponseEntity
						.badRequest()
						.body(Response.builder().error("Debe existir por lo menos 1 titular.").build()));
			}else if(quantityOwners==1) {
				isEmpresarial = quantityBusinessOwners==quantityOwners&&quantityPersonalOwners==0;
				isPersonal = quantityPersonalOwners==quantityOwners&&quantityBusinessOwners==0;
			}
			if(isPersonal) {
				return service.findAll()
				.collectList()
				.flatMap(p->{
					int i=0;
					for (Purchase purchase2 : p) {
						for (Customer customer : purchase2.getCustomerOwner()) {
							for (Customer customer2 : purchasebd.getCustomerOwner()) {
								if(customer.getIdentityNumber().equals(customer2.getIdentityNumber())
										&& purchase2.getProduct().getId().equals(purchasebd.getProduct().getId())) {
									i++;
								}
							}
						}
					}
					if(i>0) {
						return Mono.just(ResponseEntity
								.badRequest()
								.body(Response.builder().error("El cliente ya cuenta con el producto "+purchasebd.getProduct().getProductType().concat("-")
										.concat(purchasebd.getProduct().getProductName())).build()));
					}
					return service.create(purchasebd)
							.flatMap(createdObject -> Mono.just(ResponseEntity
									.created(URI.create(request.getURI().toString().concat(createdObject.getId())))
									.contentType(MediaType.APPLICATION_JSON)
									.body(Response.builder().data(createdObject).build())));
				});
			}else if(isEmpresarial) {
				System.out.println("es empresarial");
				if(!purchasebd.toBuilder().build().getProduct().getCondition()
						.getCustomerTypeTarget().stream().filter(o->o.equals("EMPRESARIAL")).findFirst().isPresent()) {
					return Mono.just(ResponseEntity
							.badRequest()
							.body(Response.builder().error("No se puede asignar el producto "+purchasebd.getProduct().getProductType().concat("-")
									.concat(purchasebd.getProduct().getProductName().concat(" a un cliente EMPRESARIAL"))).build()));
				}
			}
			return service.create(purchasebd)
					.flatMap(createdObject -> Mono.just(ResponseEntity
							.created(URI.create(request.getURI().toString().concat(createdObject.getId())))
							.contentType(MediaType.APPLICATION_JSON)
							.body(Response.builder().data(createdObject).build())));
			//return Mono.just(ResponseEntity.ok().body(Response.builder().data(purchasebd).build()));
		});
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Purchase>> update(@RequestBody Purchase purchase, @PathVariable("id") String id){
		
		Mono<Purchase> customerModification = Mono.just(purchase);
		
		Mono<Purchase> customerDatabase = service.findById(id);
		
		return customerDatabase
				.zipWith(customerModification, (a,b) -> {
					a.setId(id);
					a.setProduct(purchase.getProduct());
					a.setCustomerOwner(purchase.getCustomerOwner());
					a.setAuthorizedSigner(purchase.getAuthorizedSigner());
					a.setAmountFin(purchase.getAmountFin());
					a.setPurchaseDate(purchase.getPurchaseDate());
					return a;
				})
				.flatMap(service::update)
				.flatMap(objectUpdated -> {
					producer.send(objectUpdated);
					return Mono.just(ResponseEntity
							.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(objectUpdated));
				})
				.defaultIfEmpty(ResponseEntity
						.noContent()
						.build());
		
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<String>> delete(@PathVariable("id") String id){
		
		return service.delete(id)
				.map(objectDeleted -> ResponseEntity
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(""))
				.defaultIfEmpty(ResponseEntity
						.noContent()
						.build());
				
	}	 
	 
}
