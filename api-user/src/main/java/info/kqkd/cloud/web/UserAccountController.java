package info.kqkd.cloud.web;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import info.kqkd.cloud.pojo.User;
import info.kqkd.cloud.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class UserAccountController {

    @Autowired
    private IUserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @Value(("${USER_LOGIN_RANDOM_STR_LEN}"))
    private Integer randomStrLength;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Map<String, Object> checkLogin(HttpSession session, User user, String verifyCode) {
        return userService.login(session, user, verifyCode);
    }


    /**
     * 用户注册和校验
     */
    @PostMapping("/register")
    public Map<String, Object> register(HttpServletRequest request, User user, String mailCode, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        if (user == null) {
            map.put("result", false);
            map.put("msg", "信息有误，请重新填写！");
            return map;
        }
        String code = (String) request.getSession().getAttribute(user.getUserEmail());
        // 移除验证码
        session.removeAttribute(user.getUserEmail());
        if (Objects.equals(code, mailCode)) {
            user.setUserPassword(DigestUtil.md5Hex(user.getUserPassword()));
            if (userService.save(user)) {
                map.put("result", true);
                return map;
            }
        }
        map.put("result", false);
        map.put("msg", "验证码错误");
        return map;
    }


    /**
     * 生成验证码
     */
    @GetMapping("/verifyCode")
    public void verifyCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 生成验证码对象
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 100, 5, 20);
        // 将验证码字符存入session
        request.getSession().setAttribute("verifyCode", captcha.getCode());
        // 写出验证码到流中
        ServletOutputStream outputStream = response.getOutputStream();
        captcha.write(outputStream);
        outputStream.close();
    }


    /**
     * 用户登录校验的随机字符串
     */
    @GetMapping("/randomStr")
    public Map<String, Object> getRandomStr(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String str = RandomUtil.randomString(randomStrLength);
        request.getSession().setAttribute("randomStr", str);
        map.put("randomStr", str);
        return map;
    }


    /**
     * 发送邮件
     */
    @GetMapping("/mailCode")
    public Boolean sendMailCode(HttpServletRequest request, String mail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom("kqkdchen@qq.com");
        if (mail == null || "".equals(mail)) {
            return false;
        }
        messageHelper.setTo(mail);
        messageHelper.setSubject("欢迎注册SpeedPan服务");
        String mailCode = RandomUtil.randomString(5);
        request.getSession().setAttribute(mail, mailCode);
        messageHelper.setText("您的验证码为<b style='color: red'>" + mailCode + "</b>", true);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
