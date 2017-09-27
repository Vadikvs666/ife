/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;



/**
 *
 * @author vadim
 */
public class AtachmentEntity {

    private String filename;
    private MessageEntity message;
    
    public AtachmentEntity(String filename,MessageEntity message){
        this.filename=filename;
        this.message=message;
        
    }
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public Integer getMessageId(){
        return message.getId();
    }
    
    

    
}
