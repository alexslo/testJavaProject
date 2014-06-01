package de.alexslo.test.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.alexslo.test.shared.ContactDTO;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("springGwtServices/contactService")
public interface ContactService extends RemoteService {

	public void saveOrUpdate(ContactDTO contact) throws Exception;
	
	public void delete(ContactDTO contact) throws Exception;
	
	public void delete(long id) throws Exception;
	
	public ContactDTO find(long id);
	
	public List<ContactDTO> findAllEntries();
	
}