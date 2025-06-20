package com.example.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.exam.common.Result;
import com.example.exam.entity.User;
import com.example.exam.mapper.UserMapper;
import com.example.exam.security.ExamUserDetails;
import com.example.exam.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtTokenProvider jwtTokenProvider; // 注入 JwtTokenProvider
    @Autowired
    private JavaMailSender mailSender; // 注入 JavaMailSender

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 添加邮件配置
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.url}")
    private String appUrl;

    // 修改注册方法
    @Transactional
    public Result register(User user) {
        if (!user.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }

        if (userMapper.exists(new QueryWrapper<User>().eq("phone", user.getPhone()))) {
            return Result.error("手机号已注册");
        }

        user.setPassword(encryptPassword(user.getPassword()));
        user.setIsAdmin(false);
        user.setIsActive(false); // 注册后默认未激活

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName("用户_" + user.getPhone().substring(7));
        }

        int result = userMapper.insert(user);
        if (result > 0) {
            // 注册成功后发送激活邮件
            sendActivationEmail(user);
            return Result.success("注册成功，请查看邮箱激活账号");
        } else {
            return Result.error("注册失败");
        }
    }

    // 发送激活邮件
    private void sendActivationEmail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("请激活您的账户");

            String activationLink = appUrl + "/api/user/activate?token=" + generateActivationToken(user);

            String emailContent = "亲爱的 " + user.getName() + "同学,\n\n" +
                    "感谢您注册我们的服务！\n\n" +
                    "请单击下面的链接以激活您的帐户:\n" +
                    activationLink + "\n\n" +
                    "如果链接无法点击，请复制到浏览器地址栏访问。\n\n" +
                    "祝您使用愉快！\n\n" +
                    "  ╭(●｀∀´●)╯\n" +
                    "  ╰(●'◡'●)╮\n" +
                    "\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\n" +
                    "\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\n" +
                    "\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\n" +
                    "\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\n" +
                    "\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\n" +
                    "\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\uD83D\uDFE6\n" +
                    "\uD83D\uDFE6\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE6\n" +
                    "\uD83D\uDFEB\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFEB\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFEB\n" +
                    "\uD83D\uDFEB\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFEB\n" +
                    "\uD83D\uDFEB\uD83D\uDFE8\uD83D\uDFEA\uD83D\uDFEA\uD83D\uDFEA\uD83D\uDFE8\uD83D\uDFEA\uD83D\uDFE8\uD83D\uDFEA\uD83D\uDFE8\uD83D\uDFEB\n" +
                    "\uD83D\uDFEB\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFEA\uD83D\uDFE8\uD83D\uDFEA\uD83D\uDFE8\uD83D\uDFEA\uD83D\uDFE8\uD83D\uDFEB\n" +
                    "\uD83D\uDFEB\uD83D\uDFE8\uD83D\uDFEA\uD83D\uDFEA\uD83D\uDFEA\uD83D\uDFE8\uD83D\uDFEA\uD83D\uDFEA\uD83D\uDFEA\uD83D\uDFE8\uD83D\uDFEB\n" +
                    "\uD83D\uDFEB\uD83D\uDFE8\uD83D\uDFEA\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFEA\uD83D\uDFE8\uD83D\uDFEB\n" +
                    "\uD83D\uDFEB\uD83D\uDFE8\uD83D\uDFEA\uD83D\uDFEA\uD83D\uDFEA\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFEA\uD83D\uDFE8\uD83D\uDFEB\n" +
                    "\uD83D\uDFEB\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFE8\uD83D\uDFEB孩子们我回来了！⠀";

            message.setText(emailContent);

            mailSender.send(message);
            logger.info("激活邮件已发送至: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("发送激活邮件失败: {}", e.getMessage());
        }
    }

    // 生成激活令牌
    private String generateActivationToken(User user) {
        return jwtTokenProvider.generateToken(
                new ExamUserDetails(
                        user.getPhone(),
                        user.getPassword(),
                        getAuthorities(user.getIsAdmin()),
                        user.getIsActive()
                )
        );
    }

    // 用户登录
    @Transactional(readOnly = true)
    public Result login(String phone, String password) {
        User user = userMapper.selectOne(
                new QueryWrapper<User>()
                        .eq("phone", phone)
                        .select("id", "phone", "password", "name", "is_admin", "is_active", "email") // 确保查询email字段
        );

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return Result.error("手机号或密码错误");
        }

        if (!user.getIsActive()) {
            return Result.error("账户已被禁用");
        }


        String token = generateToken(user);
        logger.info("生成的令牌:{}", token);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", buildUserInfo(user));

        return Result.success(data);
    }

    // 修改密码
    @Transactional
    public Result changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("旧密码不正确");
        }

        user.setPassword(encryptPassword(newPassword));
        return userMapper.updateById(user) > 0 ?
                Result.success("密码修改成功") :
                Result.error("密码修改失败");
    }

    // 管理员功能：设置/取消管理员权限
    @Transactional
    public Result setAdminPrivilege(Long userId, boolean isAdmin) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if ("admin".equals(user.getPhone())) {
            return Result.error("无法修改超级管理员状态");
        }

        user.setIsAdmin(isAdmin);
        return userMapper.updateById(user) > 0 ?
                Result.success(isAdmin ? "已设为管理员" : "已取消管理员权限") :
                Result.error("操作失败");
    }

    // 实现 UserDetailsService 接口的方法
    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("phone", phone));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new ExamUserDetails(
                user.getPhone(),
                user.getPassword(),
                getAuthorities(user.getIsAdmin()),
                user.getIsActive()
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Boolean isAdmin) {
        return isAdmin ?
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")) :
                List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // 辅助方法
    private String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private String generateToken(User user) {
        ExamUserDetails userDetails = new ExamUserDetails(
                user.getPhone(),
                user.getPassword(),
                getAuthorities(user.getIsAdmin()),
                user.getIsActive()
        );
        return jwtTokenProvider.generateToken(userDetails);
    }

    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("phone", user.getPhone());
        info.put("name", user.getName());
        info.put("isAdmin", Boolean.TRUE.equals(user.getIsAdmin()));
        return info;
    }

    public User findUserByPhone(String phone) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("phone", phone));
    }

    // 管理员注册
    @Transactional
    public Result adminRegister(User user) {
        if (userMapper.exists(new QueryWrapper<User>().eq("phone", user.getPhone()))) {
            return Result.error("手机号已注册");
        }

        user.setIsAdmin(true); // 设置为管理员
        user.setIsActive(true);

        // 确保密码被加密
        user.setPassword(encryptPassword(user.getPassword()));

        if (user.getName() == null || user.getName().isEmpty()) {
            String phone = user.getPhone();
            String suffix = phone.length() >= 8 ? phone.substring(7) : "";
            user.setName("管理员_" + suffix);
        }

        return userMapper.insert(user) > 0 ?
                Result.success("管理员注册成功") :
                Result.error("管理员注册失败");
    }

    // 管理员登录
    @Transactional(readOnly = true)
    public Result adminLogin(String phone, String password) {
        // 根据手机号查询用户
        User user = userMapper.selectOne(
                new QueryWrapper<User>()
                        .eq("phone", phone)
                        .select("id", "phone", "password", "is_admin", "is_active")
        );

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return Result.error("手机号或密码错误");
        }

        if (!user.getIsActive()) {
            return Result.error("账户已被禁用");
        }

        // 检查是否是管理员
        if (!user.getIsAdmin()) {
            return Result.error("您不是管理员");
        }

        // 生成令牌
        String token = generateToken(user);

        // 构建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", buildUserInfo(user));

        return Result.success(data);
    }
    public List<User> listUsers() {
        return userMapper.selectList(null);
    }
}