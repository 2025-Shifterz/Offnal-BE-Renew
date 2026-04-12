package com.offnal.shifterz.domain.organization.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrganization is a Querydsl query type for Organization
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrganization extends EntityPathBase<Organization> {

    private static final long serialVersionUID = -2069097888L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrganization organization = new QOrganization("organization");

    public final com.offnal.shifterz.global.QBaseTimeEntity _super = new com.offnal.shifterz.global.QBaseTimeEntity(this);

    public final ListPath<com.offnal.shifterz.domain.work.domain.WorkCalendar, com.offnal.shifterz.domain.work.domain.QWorkCalendar> calendars = this.<com.offnal.shifterz.domain.work.domain.WorkCalendar, com.offnal.shifterz.domain.work.domain.QWorkCalendar>createList("calendars", com.offnal.shifterz.domain.work.domain.WorkCalendar.class, com.offnal.shifterz.domain.work.domain.QWorkCalendar.class, PathInits.DIRECT2);

    //inherited
    public final NumberPath<Long> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.offnal.shifterz.domain.member.domain.QMember organizationMember;

    public final StringPath organizationName = createString("organizationName");

    public final StringPath team = createString("team");

    //inherited
    public final NumberPath<Long> updatedAt = _super.updatedAt;

    public QOrganization(String variable) {
        this(Organization.class, forVariable(variable), INITS);
    }

    public QOrganization(Path<? extends Organization> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrganization(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrganization(PathMetadata metadata, PathInits inits) {
        this(Organization.class, metadata, inits);
    }

    public QOrganization(Class<? extends Organization> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.organizationMember = inits.isInitialized("organizationMember") ? new com.offnal.shifterz.domain.member.domain.QMember(forProperty("organizationMember")) : null;
    }

}

