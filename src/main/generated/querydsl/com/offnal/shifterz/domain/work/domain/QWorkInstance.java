package com.offnal.shifterz.domain.work.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkInstance is a Querydsl query type for WorkInstance
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkInstance extends EntityPathBase<WorkInstance> {

    private static final long serialVersionUID = -1369194287L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkInstance workInstance = new QWorkInstance("workInstance");

    public final com.offnal.shifterz.global.QBaseTimeEntity _super = new com.offnal.shifterz.global.QBaseTimeEntity(this);

    //inherited
    public final NumberPath<Long> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final NumberPath<Long> updatedAt = _super.updatedAt;

    public final QWorkCalendar workCalendar;

    public final DatePath<java.time.LocalDate> workDate = createDate("workDate", java.time.LocalDate.class);

    public final EnumPath<WorkTimeType> workTimeType = createEnum("workTimeType", WorkTimeType.class);

    public QWorkInstance(String variable) {
        this(WorkInstance.class, forVariable(variable), INITS);
    }

    public QWorkInstance(Path<? extends WorkInstance> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkInstance(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkInstance(PathMetadata metadata, PathInits inits) {
        this(WorkInstance.class, metadata, inits);
    }

    public QWorkInstance(Class<? extends WorkInstance> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.workCalendar = inits.isInitialized("workCalendar") ? new QWorkCalendar(forProperty("workCalendar"), inits.get("workCalendar")) : null;
    }

}

