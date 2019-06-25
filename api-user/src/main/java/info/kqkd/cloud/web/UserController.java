package info.kqkd.cloud.web;


import info.kqkd.cloud.pojo.User;
import info.kqkd.cloud.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 可圈可丶
 * @since 2019-06-12
 */
@RestController
@RequestMapping("")
public class UserController {

    @Autowired
    private IUserService userService;


    @GetMapping("/{id}")
    public User getById(@PathVariable("id") Integer id) {
        return userService.getById(id);
    }


    @PutMapping("/add")
    public User addUser(User user) {
        userService.save(user);
        System.out.println(user);
        return user;
    }

    @GetMapping("/get")
    public User getUser(String userAccount) {
        User currentUser = userService.getCurrentUser(userAccount);
        System.out.println(currentUser);
        return currentUser;
    }



}
