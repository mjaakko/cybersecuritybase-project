package sec.project.controller;


import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private SignupRepository signupRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/view", method = RequestMethod.POST)
    public String view(Model model, @RequestParam String name) throws SQLException {
        //SQL injection
        ResultSet rs = jdbcTemplate.getDataSource().getConnection().prepareStatement("SELECT * FROM Signup WHERE name = '"+name+"'").executeQuery();
        if (rs.next()) {
            Signup signup = new Signup(rs.getString("name"), rs.getString("address"));

            //XSS is also possible as the signup details can contain HTML which will be displayed as-is without filtering
            model.addAttribute("signup", signup);
        }
        
        return "view";
    }
    
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@RequestParam String name) {
        //Broken access control
        signupRepository.delete(signupRepository.findAll().stream().filter(s -> s.getName().equals(name)).findAny().get());
        
        return "deleted";
    }
    
    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String address) {
        signupRepository.save(new Signup(name, address));
        return "done";
    }

}
