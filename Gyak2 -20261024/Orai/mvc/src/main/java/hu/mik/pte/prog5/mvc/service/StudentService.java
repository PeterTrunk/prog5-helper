package hu.mik.pte.prog5.mvc.service;

import hu.mik.pte.prog5.mvc.model.Student;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentService {
    Map<Long, Student> studentList = new HashMap<Long, Student>();

    @PostConstruct
    private void init() {
        this.studentList.put(0L, new Student(0L, "Bob"));
        this.studentList.put(1L, new Student(1L, "Tom"));
        this.studentList.put(2L, new Student(2L, "John"));
    }

    public String getStudentNameById(Long id) {
        if (!this.studentList.containsKey(id)) {
            throw new RuntimeException("Missing ID");
        }

        return this.studentList.get(id).getName();

    }

    public List<Student> findByName(String name) {
        return this.studentList.values().stream()
                .filter(s -> s.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    public void save(Student student) {
        if (student.getId() == null) {
            var newId = (long) this.studentList.size();
            student.setId(newId);
            this.studentList.put(newId, student);
            //Create new
            return;
        }
        //Update existing
        Optional.ofNullable(this.studentList.get(student.getId()))
                .map(existing -> {
                    existing.setName(student.getName());
                    return existing;
                }).orElseThrow();
    }
}
