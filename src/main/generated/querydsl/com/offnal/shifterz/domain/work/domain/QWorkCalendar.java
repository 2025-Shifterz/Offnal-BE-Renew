package com.offnal.shifterz.domain.work.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkCalendar is a Querydsl query type for WorkCalendar
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkCalendar extends EntityPathBase<WorkCalendar> {

    private static final long serialVersionUID = -2102646918L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkCalendar workCalendar = new QWorkCalendar("workCalendar");

    public final com.offnal.shifterz.global.QBaseTimeEntity _super = new com.offnal.shifterz.global.QBaseTimeEntity(this);

    //inherited
    public final NumberPath<Long> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final com.offnal.shifterz.domain.organization.domain.QOrganization organization;

    //inherited
    public final NumberPath<Long> updatedAt = _super.updatedAt;

    public final ListPath<WorkInstance, QWorkInstance> workInstances = this.<WorkInstance, QWorkInstance>createList("workInstances", WorkInstance.class, QWorkInstance.class, PathInits.DIRECT2);

    public final MapPath<String, WorkTime, QWorkTime> workTimes = this.<String, WorkTime, QWorkTime>createMap("workTimes", String.class, WorkTime.class, QWorkTime.class);

    public QWorkCalendar(String variable) {
        this(WorkCalendar.class, forVariable(variable), INITS);
    }

    public QWorkCalendar(Path<? extends WorkCalendar> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkCalendar(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkCalendar(PathMetadata metadata, PathInits inits) {
        this(WorkCalendar.class, metadata, inits);
    }

    public QWorkCalendar(Class<? extends WorkCalendar> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.organization = inits.isInitialized("organization") ? new com.offnal.shifterz.domain.organization.domain.QOrganization(forProperty("organization"), inits.get("organization")) : null;
    }

}

