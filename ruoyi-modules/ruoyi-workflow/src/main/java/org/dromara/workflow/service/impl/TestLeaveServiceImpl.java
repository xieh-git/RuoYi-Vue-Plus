package org.dromara.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.event.ProcessEvent;
import org.dromara.common.core.domain.event.ProcessTaskEvent;
import org.dromara.common.core.enums.BusinessStatusEnum;
import org.dromara.common.core.service.WorkflowService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.workflow.domain.TestLeave;
import org.dromara.workflow.domain.bo.TestLeaveBo;
import org.dromara.workflow.domain.vo.TestLeaveVo;
import org.dromara.workflow.mapper.TestLeaveMapper;
import org.dromara.workflow.service.ITestLeaveService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 请假Service业务层处理
 *
 * @author may
 * @date 2023-07-21
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class TestLeaveServiceImpl implements ITestLeaveService {

    private final TestLeaveMapper baseMapper;
    private final WorkflowService workflowService;

    /**
     * 查询请假
     */
    @Override
    public TestLeaveVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询请假列表
     */
    @Override
    public TableDataInfo<TestLeaveVo> queryPageList(TestLeaveBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<TestLeave> lqw = buildQueryWrapper(bo);
        Page<TestLeaveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询请假列表
     */
    @Override
    public List<TestLeaveVo> queryList(TestLeaveBo bo) {
        LambdaQueryWrapper<TestLeave> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<TestLeave> buildQueryWrapper(TestLeaveBo bo) {
        LambdaQueryWrapper<TestLeave> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getLeaveType()), TestLeave::getLeaveType, bo.getLeaveType());
        lqw.ge(bo.getStartLeaveDays() != null, TestLeave::getLeaveDays, bo.getStartLeaveDays());
        lqw.le(bo.getEndLeaveDays() != null, TestLeave::getLeaveDays, bo.getEndLeaveDays());
        lqw.orderByDesc(BaseEntity::getCreateTime);
        return lqw;
    }

    /**
     * 新增请假
     */
    @Override
    public TestLeaveVo insertByBo(TestLeaveBo bo) {
        TestLeave add = MapstructUtils.convert(bo, TestLeave.class);
        if (StringUtils.isBlank(add.getStatus())) {
            add.setStatus(BusinessStatusEnum.DRAFT.getStatus());
        }
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return MapstructUtils.convert(add, TestLeaveVo.class);
    }

    /**
     * 修改请假
     */
    @Override
    public TestLeaveVo updateByBo(TestLeaveBo bo) {
        TestLeave update = MapstructUtils.convert(bo, TestLeave.class);
        baseMapper.updateById(update);
        return MapstructUtils.convert(update, TestLeaveVo.class);
    }

    /**
     * 批量删除请假
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        List<String> idList = StreamUtils.toList(ids, String::valueOf);
        workflowService.deleteRunAndHisInstance(idList);
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 执行办理任务监听
     *
     * @param processTaskEvent 参数
     */
    @EventListener(condition = "#processTaskEvent.keyNode=='leave1_Activity_14633hx'")
    public void leave1Task(ProcessTaskEvent processTaskEvent) {
        log.info("当前任务执行了{}", processTaskEvent.toString());
        TestLeave testLeave = baseMapper.selectById(Long.valueOf(processTaskEvent.getBusinessKey()));
        testLeave.setStatus(BusinessStatusEnum.WAITING.getStatus());
        baseMapper.updateById(testLeave);
    }


    /**
     * 总体流程监听(例如: 提交 退回 撤销 终止 作废等)
     *
     * @param processEvent 参数
     */
    @EventListener(condition = "#processEvent.key=='leave1'")
    public void leave1(ProcessEvent processEvent) {
        log.info("当前任务执行了{}", processEvent.toString());
        TestLeave testLeave = baseMapper.selectById(Long.valueOf(processEvent.getBusinessKey()));
        testLeave.setStatus(processEvent.getStatus());
        if (processEvent.isSubmit()) {
            testLeave.setStatus(BusinessStatusEnum.WAITING.getStatus());
        }
        baseMapper.updateById(testLeave);
    }

    /**
     * 总体流程监听(例如: 提交 退回 撤销 终止 作废等)
     *
     * @param processEvent 参数
     */
    @EventListener(condition = "#processEvent.key=='leave2'")
    public void leave2(ProcessEvent processEvent) {
        log.info("当前任务执行了{}", processEvent.toString());
        TestLeave testLeave = baseMapper.selectById(Long.valueOf(processEvent.getBusinessKey()));
        testLeave.setStatus(processEvent.getStatus());
        if (processEvent.isSubmit()) {
            testLeave.setStatus(BusinessStatusEnum.WAITING.getStatus());
        }
        baseMapper.updateById(testLeave);
    }

    /**
     * 总体流程监听(例如: 提交 退回 撤销 终止 作废等)
     *
     * @param processEvent 参数
     */
    @EventListener(condition = "#processEvent.key=='leave3'")
    public void leave3(ProcessEvent processEvent) {
        log.info("当前任务执行了{}", processEvent.toString());
        TestLeave testLeave = baseMapper.selectById(Long.valueOf(processEvent.getBusinessKey()));
        testLeave.setStatus(processEvent.getStatus());
        if (processEvent.isSubmit()) {
            testLeave.setStatus(BusinessStatusEnum.WAITING.getStatus());
        }
        baseMapper.updateById(testLeave);
    }

    /**
     * 总体流程监听(例如: 提交 退回 撤销 终止 作废等)
     *
     * @param processEvent 参数
     */
    @EventListener(condition = "#processEvent.key=='leave4'")
    public void leave4(ProcessEvent processEvent) {
        log.info("当前任务执行了{}", processEvent.toString());
        TestLeave testLeave = baseMapper.selectById(Long.valueOf(processEvent.getBusinessKey()));
        testLeave.setStatus(processEvent.getStatus());
        if (processEvent.isSubmit()) {
            testLeave.setStatus(BusinessStatusEnum.WAITING.getStatus());
        }
        baseMapper.updateById(testLeave);
    }

    /**
     * 总体流程监听(例如: 提交 退回 撤销 终止 作废等)
     *
     * @param processEvent 参数
     */
    @EventListener(condition = "#processEvent.key=='leave5'")
    public void leave5(ProcessEvent processEvent) {
        log.info("当前任务执行了{}", processEvent.toString());
        TestLeave testLeave = baseMapper.selectById(Long.valueOf(processEvent.getBusinessKey()));
        testLeave.setStatus(processEvent.getStatus());
        if (processEvent.isSubmit()) {
            testLeave.setStatus(BusinessStatusEnum.WAITING.getStatus());
        }
        baseMapper.updateById(testLeave);
    }

    /**
     * 总体流程监听(例如: 提交 退回 撤销 终止 作废等)
     *
     * @param processEvent 参数
     */
    @EventListener(condition = "#processEvent.key=='leave6'")
    public void leave6(ProcessEvent processEvent) {
        log.info("当前任务执行了{}", processEvent.toString());
        TestLeave testLeave = baseMapper.selectById(Long.valueOf(processEvent.getBusinessKey()));
        testLeave.setStatus(processEvent.getStatus());
        if (processEvent.isSubmit()) {
            testLeave.setStatus(BusinessStatusEnum.WAITING.getStatus());
        }
        baseMapper.updateById(testLeave);
    }
}
