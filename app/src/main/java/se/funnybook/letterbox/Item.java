package se.funnybook.letterbox;

public class Item {
    private String name;
    private byte[] id;

    public Item(String name, byte[] id) {
        this.name = name;
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public byte[] getId() {
        return id;
    }
    public void setId(byte[] id) {
        this.id = id;
    }
}
