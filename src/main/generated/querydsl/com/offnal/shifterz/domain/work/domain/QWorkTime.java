package com.offnal.shifterz.domain.work.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWorkTime is a Querydsl query type for WorkTime
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QWorkTime extends BeanPath<WorkTime> {

    private static final long serialVersionUID = 10157321L;

    public static final QWorkTime workTime = new QWorkTime("workTime");

    public final com.offnal.shifterz.global.QBaseTimeEntity _super = new com.offnal.shifterz.global.QBaseTimeEntity(this);

    //inherited
    public final NumberPath<Long> createdAt = _super.createdAt;

    public final ComparablePath<java.time.Duration> duration = createComparable("duration", java.time.Duration.class);

    public final TimePath<java.time.LocalTime> startTime = createTime("startTime", java.time.LocalTime.class);

    public final EnumPath<WorkTimeType> timeType = createEnum("timeType", WorkTimeType.class);

    //inherited
    public final NumberPath<Long> updatedAt = _super.updatedAt;

    public QWorkTime(String variable) {
        super(WorkTime.class, forVariable(variable));
    }

    public QWorkTime(Path<? extends WorkTime> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWorkTime(PathMetadata metadata) {
        super(WorkTime.class, metadata);
    }

}

