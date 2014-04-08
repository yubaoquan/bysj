package beans;

import javax.mail.Part;

public class AttachmentBean {
	private int id;
	private String title;
	private Part part;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public AttachmentBean(int id, String title, Part part) {
		super();
		this.id = id;
		this.title = title;
		this.part = part;
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
