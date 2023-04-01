package com.rookie.rookiemeeting.service.Impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rookie.rookiemeeting.common.lang.Constants;
import com.rookie.rookiemeeting.common.lang.Result;
import com.rookie.rookiemeeting.dto.EmployeeDto;
import com.rookie.rookiemeeting.dto.ParticipantsDto;
import com.rookie.rookiemeeting.dto.UpdatePassDTO;
import com.rookie.rookiemeeting.entity.Employee;
import com.rookie.rookiemeeting.mapper.EmployeeMapper;
import com.rookie.rookiemeeting.mapper.MeetingParticipantsMapper;
import com.rookie.rookiemeeting.service.IEmployeeService;
import com.rookie.rookiemeeting.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.rookie.rookiemeeting.util.RedisConstants.LOGIN_CODE_KEY;
import static com.rookie.rookiemeeting.util.RedisConstants.LOGIN_CODE_TTL;

@Service
@Transactional
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    MeetingParticipantsMapper meetingParticipantsMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //注册
    @Override
    public int register(Employee employee) {
        employee.setStatus(Constants.STATUS);
        employee.setRole(Constants.ROLE);
        employee.setPassword(DigestUtils.md5Hex(employee.getPassword() + Constants.SALT));
        return employeeMapper.register(employee);
    }
    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1.校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        // 3.符合，生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 4.保存验证码到 session
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

        // 5.发送验证码
        log.debug("发送短信验证码成功，验证码：{}", code);
        // 返回ok
        return Result.succ(code);
    }


    //查询用户名是否存在
    @Override
    public Employee queryUsername(String username) {
        return employeeMapper.queryUsername(username);
    }

    //修改密码
    @Override
    public int updatePass(UpdatePassDTO updatePassDTO) {
        updatePassDTO.setPassword(DigestUtils.md5Hex(updatePassDTO.getPassword() + Constants.SALT));
        updatePassDTO.setNewPassword(DigestUtils.md5Hex(updatePassDTO.getNewPassword() + Constants.SALT));
        return employeeMapper.updatePass(updatePassDTO);
    }

    //分页查询
    @Override
    public Page<Employee> findEmployee(Page<Employee> page, String employeename, String phone, String email, String departmentname) {
        return employeeMapper.findEmployee(page, employeename, phone, email, departmentname);
    }

    //查询所有审批状态为0的员工
    @Override
    public List<Employee> getByStatus(Integer status) {
        return employeeMapper.getByStatus(status);
    }

    //根据ID通过审批
    @Override
    public Integer updateStatusAdopt(Long employeeid) {
        return employeeMapper.updateStatusAdopt(employeeid);
    }

    //根据ID不通过审批
    @Override
    public Integer updateStatusFail(Long employeeid) {
        return employeeMapper.updateStatusFail(employeeid);
    }

    //根据会议ID查询会议表信息
    @Override
    public List<ParticipantsDto> getEmpsById(Integer meetingid) {
        return employeeMapper.getAllEmpsById(meetingid);
    }

    //查询员工，状态为1的
    @Override
    public List<Employee> getAll() {
        return employeeMapper.getAll();
    }

    //导出
    @Override
    public List<EmployeeDto> export() {
        return employeeMapper.export();
    }


}
