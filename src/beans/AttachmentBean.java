package beans;

import javax.mail.Part;

public class AttachmentBean implements LabelBean{
	private int id;
	private int mailID;
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

	private String title;
	private Part part;
	private int offset;
	private String location;
	
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

	public AttachmentBean(int id, String title, Part part) {
		super();
		this.id = id;
		this.title = title;
		this.part = part;
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
