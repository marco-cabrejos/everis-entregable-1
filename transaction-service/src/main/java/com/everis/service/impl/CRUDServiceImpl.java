package com.everis.service.impl;

import com.everis.repository.IRepository;
import com.everis.service.ICRUDService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class CRUDServiceImpl<T, ID> implements ICRUDService<T, ID> {
	
	protected abstract IRepository<T, ID> getRepository();

	@Override
	public Mono<T> create(T o) {
		return getRepository().save(o);
	}

	@Override
	public Flux<T> findAll() {
		return getRepository().findAll();
	}
	
	@Override
	public Mono<T> findById(ID id) {
		return getRepository().findById(id);
	}

	@Override
	public Mono<T> update(T o) {
		return getRepository().save(o);
	}

	@Override
	public Mono<Void> delete(ID id) {
		return getRepository().deleteById(id);
	}

}
