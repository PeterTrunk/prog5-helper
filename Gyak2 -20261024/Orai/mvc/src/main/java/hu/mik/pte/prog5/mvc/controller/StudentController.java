package hu.mik.pte.prog5.mvc.controller;

import hu.mik.pte.prog5.mvc.model.Student;
import hu.mik.pte.prog5.mvc.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

//    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

//    @RequestMapping(value = "/students",method = RequestMethod.GET)
    @GetMapping("/{id}")
//    @GetMapping("/students/{name}/{age}/{id}")
    @ResponseBody
    public String getStudentNameById(@PathVariable(value = "id") Long studentId){
        return this.studentService.getStudentNameById(studentId).toString();
//        return "students";
    }

    @GetMapping("/search")
    //localhost:8090/students/search?name=
    public String search(@RequestParam("name") String name, Model model){
        model.addAttribute("results",this.studentService.findByName(name));
        return "search-result";
    }

    //@RequestMapping(value = "/students",method = RequestMethod.POST)
    @PostMapping()
    @ResponseBody
    public void addStudent(@RequestBody Student student){
        this.studentService.save(student);
    }


}
