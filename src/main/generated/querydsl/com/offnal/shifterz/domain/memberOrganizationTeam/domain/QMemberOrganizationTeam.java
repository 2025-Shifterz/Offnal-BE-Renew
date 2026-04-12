package com.offnal.shifterz.domain.memberOrganizationTeam.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberOrganizationTeam is a Querydsl query type for MemberOrganizationTeam
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberOrganizationTeam extends EntityPathBase<MemberOrganizationTeam> {

    private static final long serialVersionUID = -1424235058L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberOrganizationTeam memberOrganizationTeam = new QMemberOrganizationTeam("memberOrganizationTeam");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.offnal.shifterz.domain.member.domain.QMember member;

    public final com.offnal.shifterz.domain.organization.domain.QOrganization organization;

    public final StringPath team = createString("team");

    public QMemberOrganizationTeam(String variable) {
        this(MemberOrganizationTeam.class, forVariable(variable), INITS);
    }

    public QMemberOrganizationTeam(Path<? extends MemberOrganizationTeam> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberOrganizationTeam(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberOrganizationTeam(PathMetadata metadata, PathInits inits) {
        this(MemberOrganizationTeam.class, metadata, inits);
    }

    public QMemberOrganizationTeam(Class<? extends MemberOrganizationTeam> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.offnal.shifterz.domain.member.domain.QMember(forProperty("member")) : null;
        this.organization = inits.isInitialized("organization") ? new com.offnal.shifterz.domain.organization.domain.QOrganization(forProperty("organization"), inits.get("organization")) : null;
    }

}

