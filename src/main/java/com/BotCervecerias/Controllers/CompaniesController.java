package com.BotCervecerias.Controllers;

import com.BotCervecerias.Models.Companies;
import com.BotCervecerias.Models.Users;
import com.BotCervecerias.Repositories.CompaniesRepository;
import com.BotCervecerias.Repositories.UserRepository;
import com.BotCervecerias.Services.CompaniesService;
import com.BotCervecerias.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
public class CompaniesController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompaniesRepository companiesRepository;
    @Autowired
    private CompaniesService companiesService;
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/getInfoCompanies")
    public ResponseEntity<Companies> getInfoCompany(@RequestHeader("Authorization") String token) {
        try {

            // Obtener el usuario autenticado
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtils.getEmailToken(jwtToken);
            Optional<Users> userModel = userRepository.findByEmail(email);
            if (userModel.isPresent()) {
                Long userId = userModel.get().getId();
                Optional<Companies> existingCompany = companiesRepository.findByUsers_Id(userId);
                Companies company;
                if (existingCompany.isPresent()) {
                    // Si la empresa ya existe, devolver la información existente
                    company = existingCompany.get();
                } else {
                    // Si la empresa no existe, crear una nueva y asociarla al usuario
                    company = new Companies();
                    company.setName(userModel.get().getName());
                    company.setUsers(userId);
                    company.setLogo("logo");
                    company.setAddres("Default Adress");

                    company = companiesRepository.save(company);
                }

                return ResponseEntity.ok(company);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            return null;
        }

    }

    @PutMapping("/editInformation/{id}")
    public ResponseEntity<Companies> updateCompanyProfile(@PathVariable Long id, @RequestBody Companies profileDetails) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<Users> userModel = userRepository.findByEmail(authentication.getName());
            if(userModel.isPresent()){
                Long idUser = userModel.get().getId();
                Optional<Companies> existingCompany = companiesRepository.findByUsers_Id(idUser);
                Companies company;
                if (existingCompany.isPresent()) {
                    company  = companiesService.updateCompanyProfile(id, profileDetails);
                }else{
                    return ResponseEntity.badRequest().body(null);
                }
                return new ResponseEntity<>(company, HttpStatus.OK);
            }else {
                // Si el usuario no se encuentra, devolver un error
                return ResponseEntity.badRequest().body(null);}

        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/findByNames")
    public ResponseEntity<List<Companies>> findCompaniesByNames(@RequestBody List<String> breweryNames) {
        try {
            List<Companies> companies = companiesRepository.findByNameIn(breweryNames);
            if (companies.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(companies, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/findByName")
    public ResponseEntity<Companies> findCompaniesByName(@RequestBody String breweryName) {
        try {
            Optional<Companies> companies = companiesRepository.findByName(breweryName);
            if (companies.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(companies.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteCompany")
    public ResponseEntity<?> deleteCompany(@RequestParam Long id){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<Users> userModel = userRepository.findByEmail(authentication.getName());
            if(userModel.isPresent()) {

                Long idUser = userModel.get().getId();
                Optional<Companies> existingCompany = companiesRepository.findByUsers_Id(idUser);

                if (existingCompany.isPresent()) {
                    companiesService.deleteCompany(userModel,id);
                    userRepository.deleteById(idUser);

                }
            }return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }
}
