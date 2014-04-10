package beans;

import javax.mail.Part;

import client.net.down.ReceiveMail;

public class AttachmentBean implements LabelBean {
	private int id;
	private int mailID = -1;
	private String owner;

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	private String title;
	private Part part;
	private int offset;
	private String location;
	private ReceiveMail receiveMail;
	
	public int getMailID() {
		return mailID;
	}

	public void setMailID(int mailID) {
		this.mailID = mailID;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTitle() {
		return title;
	}


	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public int getID() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getSubject() {
		return title;
	}

	public AttachmentBean(int id, String title, Part part, ReceiveMail receiveMail) {
		super();
		this.id = id;
		this.title = title;
		this.part = part;
		this.receiveMail = receiveMail;
	}

	public ReceiveMail getReceiveMail() {
		return receiveMail;
	}

	public void setReceiveMail(ReceiveMail receiveMail) {
		this.receiveMail = receiveMail;
	}

	public AttachmentBean() {

	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Part getPart() {
		return part;
	}

	public void setPart(Part part) {
		this.part = part;
	}
}
