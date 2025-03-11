package Cliente.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ServiceOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String description;
    private LocalDateTime requestTime;
    
    public ServiceOrder(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.requestTime = LocalDateTime.now();
    }
    
    // Getters e Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getRequestTime() {
        return requestTime;
    }
    
    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
    
    @Override
    public String toString() {
        return "OS: " + code + 
               "\nNome: " + name + 
               "\nDescrição: " + description + 
               "\nData/Hora da Solicitação: " + requestTime;
    }
}