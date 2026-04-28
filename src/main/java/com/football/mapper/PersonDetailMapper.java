package com.football.mapper;

import com.football.entity.PersonDetail;
import com.football.vo.PersonDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 人员详细信息数据访问层
 *
 * @author system
 * @version 1.0.0
 */
@Mapper
public interface PersonDetailMapper {

    /**
     * 根据人员ID查询详细信息
     *
     * @param personId 人员ID
     * @return 人员详细信息VO
     */
    PersonDetailVO selectByPersonId(@Param("personId") Long personId);

    /**
     * 插入人员详细信息
     *
     * @param personDetail 人员详细信息实体
     * @return 受影响的行数
     */
    int insert(PersonDetail personDetail);

    /**
     * 根据人员ID更新详细信息
     *
     * @param personDetail 人员详细信息实体
     * @return 受影响的行数
     */
    int updateByPersonId(PersonDetail personDetail);

    /**
     * 根据人员ID删除详细信息（逻辑删除）
     *
     * @param personId 人员ID
     * @return 受影响的行数
     */
    int deleteByPersonId(@Param("personId") Long personId);
}
