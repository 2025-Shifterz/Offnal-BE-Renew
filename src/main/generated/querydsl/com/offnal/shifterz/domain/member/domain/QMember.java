package com.offnal.shifterz.domain.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 233287982L;

    public static final QMember member = new QMember("member1");

    public final com.offnal.shifterz.global.QBaseTimeEntity _super = new com.offnal.shifterz.global.QBaseTimeEntity(this);

    public final StringPath appleRefreshToken = createString("appleRefreshToken");

    //inherited
    public final NumberPath<Long> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memberName = createString("memberName");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath profileImageKey = createString("profileImageKey");

    public final EnumPath<Provider> provider = createEnum("provider", Provider.class);

    public final StringPath providerId = createString("providerId");

    //inherited
    public final NumberPath<Long> updatedAt = _super.updatedAt;

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

