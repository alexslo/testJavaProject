package de.alexslo.test.server;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import de.alexslo.test.shared.ContactDTO;

@Repository("contactDAO")
public class ContactDAO extends AbstractHibernateJpaDAO<Long, ContactDTO> {

	@PersistenceContext(unitName = "MyPUnit")
	EntityManager entityManager;

	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}

}