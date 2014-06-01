package de.alexslo.test.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.alexslo.test.client.ContactService;
import de.alexslo.test.shared.ContactDTO;
import de.alexslo.test.shared.Validator;

@Service("contactService")
public class ContactServiceImpl extends RemoteServiceServlet implements ContactService {
	
	private static final long serialVersionUID = -6547737229424190373L;

	private static final Log LOG = LogFactory.getLog(ContactServiceImpl.class);

	@Autowired
	private ContactDAO contactDAO;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveOrUpdate(ContactDTO contact) throws Exception {
		
	    // Verify that the input is valid.
		if (Validator.isBlank(contact.getName()) || Validator.isBlank(contact.getTelephone())) {
		      // If the input is not valid, throw an IllegalArgumentException back to
		      // the client.
		      throw new IllegalArgumentException("Please enter at least the Name and the Telephone to the telephone contact");
		}
		
		try {
			if (contact.getId() == null) {
				contactDAO.persist(contact);
			} else {
				contactDAO.merge(contact);
			}
		} catch (Exception e) {
			LOG.error(e);
			throw e;
		}
	}


	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void delete(ContactDTO contact) throws Exception {
		if (contact.getId() != null) {
			contactDAO.remove(contact);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void delete(long id) throws Exception {
		if (id != 0) {
			contactDAO.remove(contactDAO.findById(id));	
		}
	}

	public ContactDTO find(long id) {
		return contactDAO.findById(id);
	}
	
	public List<ContactDTO> findAllEntries() {
		List<ContactDTO> findAll = contactDAO.findAll();
		List<ContactDTO> result = new ArrayList<ContactDTO>();
		for (ContactDTO contact : findAll) {
			result.add(new ContactDTO(contact));
		}
		return result;
	}

}
