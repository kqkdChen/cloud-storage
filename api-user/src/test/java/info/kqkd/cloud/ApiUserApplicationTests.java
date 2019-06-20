package info.kqkd.cloud;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.Digester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@RunWith(SpringRunner.class)
@SpringBootTest
//@Ignore
public class ApiUserApplicationTests {

    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void contextLoads() {

//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setSubject("华为技术有限公司武汉地区招聘");
//        message.setText("亲爱的女晶女士/先生，我司已经确定录取您，请您准备好先关证件于6.20号办理入职手续");
//        message.setTo("2370633962@qq.com");
//        message.setFrom("657424056@qq.com");
//        mailSender.send(message);


//        Digester digester = new Digester(DigestAlgorithm.MD5);
//        System.out.println(digester.digestHex(username));

    }

    @Test
    public void test2() throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);


    }

}
