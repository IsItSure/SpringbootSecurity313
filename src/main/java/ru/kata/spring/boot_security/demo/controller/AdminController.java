package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userServiceImpl;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userServiceImpl, RoleRepository roleRepository) {
        this.userServiceImpl = userServiceImpl;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/")
    public String userList(Model model) {
        List<User> users = userServiceImpl.getUsers();
        model.addAttribute("users", users);
        return "user-list";
    }

    @GetMapping("/userForm")
    public String userForm(@RequestParam(value = "id", required = false) Long id, Model model) {
        User user;
        if (id == null) {
            user = new User();
        } else {
            user = userServiceImpl.getUserById(id);
        }
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "user-form";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("user") User user,
                           @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {
        if (roleIds != null) {
            Set<Role> roles = roleRepository.findAllById(roleIds).stream().collect(Collectors.toSet());
            user.setRoles(roles);
        }
        if ((user.getId() == null)) {
            userServiceImpl.saveUser(user);
        } else {
            userServiceImpl.updateUser(user);
        }
        return "redirect:/admin/";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id) {
        userServiceImpl.deleteUser(id);
        return "redirect:/admin/";
    }
}
