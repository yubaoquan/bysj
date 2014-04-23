package beans;

import javax.mail.Part;

import client.net.down.ReceiveMail;

public class AttachmentBean implements LabelBean {
	private int id;
	private int mailID = -1;
	private String owner;
	private String title;
	private Part part;
	private int offset;
	private String location;
	private ReceiveMail receiveMail;
	
	public AttachmentBean() {

	}
	
	public AttachmentBean(int id, String title, Part part, ReceiveMail receiveMail) {
		super();
		this.id = id;
		this.title = title;
		this.part = part;
		this.receiveMail = receiveMail;
	}
	
	@Override
	public int getID() {
		return id;
	}

	public String getLocation() {
		return location;
	}

	public int getMailID() {
		return mailID;
	}

	public int getOffset() {
		return offset;
	}

	public String getOwner() {
		return owner;
	}


	public Part getPart() {
		return part;
	}

	public ReceiveMail getReceiveMail() {
		return receiveMail;
	}

	@Override
	public String getSubject() {
		return title;
	}

	public String getTitle() {
		return title;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setMailID(int mailID) {
		this.mailID = mailID;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setPart(Part part) {
		this.part = part;
	}

	public void setReceiveMail(ReceiveMail receiveMail) {
		this.receiveMail = receiveMail;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
