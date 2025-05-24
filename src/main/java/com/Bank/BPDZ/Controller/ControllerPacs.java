package com.Bank.BPDZ.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Bank.BPDZ.DTO.Pacs008DTO;
import com.Bank.BPDZ.DTO.Pacs009DTO;
import com.Bank.BPDZ.Entity.BPDZHabi;
import com.Bank.BPDZ.Entity.BPDZmt;
import com.Bank.BPDZ.Service.PacsService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpSession;

@Controller

@RequestMapping("/Pacs")
public class ControllerPacs {
	@Autowired
	PacsService pacsService;
	
	 @PostMapping("/generatePacs008")
	    public String generateAndSavePacs008( @ModelAttribute ("pacs008DTO") Pacs008DTO dto,
	    		RedirectAttributes redirectAttributes, HttpSession session) throws Exception   {
	        BPDZHabi user = (BPDZHabi) session.getAttribute("loggedInUser");
	        if (user == null) {
	            return "redirect:/sigin";
	        }
	        try {
	           BPDZmt x= pacsService.savePacs008(dto);
	           pacsService.processPayment(x, user);
	          
	            redirectAttributes.addFlashAttribute("successMessage", "Données modifiées avec succès.");
	        } catch (Exception e) {
	        	
	            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors de la mise à jour.");
	           
	        }
	        
	        BPDZmt x= pacsService.savePacs008(dto);
	           pacsService.processPayment(x, user);
	        
	       // pacsService.jeutContablePacs008(dto,user);
	        return "redirect:/form"; // Changez selon votre besoin
	    }
	 @PostMapping("/generatePacs009")
	    public String generateAndSavePacs009( @ModelAttribute ("pacs009DTO") Pacs009DTO dto,
	    		RedirectAttributes redirectAttributes, HttpSession session) throws Exception   {
	        BPDZHabi user = (BPDZHabi) session.getAttribute("loggedInUser");
	        if (user == null) {
	            return "redirect:/sigin";
	        }
	        try {
	           BPDZmt x= pacsService.savePacs009(dto);
	           pacsService.processPayment(x, user);
	          
	            redirectAttributes.addFlashAttribute("successMessage", "Données modifiées avec succès.");
	        } catch (Exception e) {
	        	
	            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors de la mise à jour.");
	           
	        }
	        
	        BPDZmt x= pacsService.savePacs009(dto);
	           pacsService.processPayment(x, user);
	        
	       // pacsService.jeutContablePacs008(dto,user);
	        return "redirect:/form"; // Changez selon votre besoin
	    }

}
