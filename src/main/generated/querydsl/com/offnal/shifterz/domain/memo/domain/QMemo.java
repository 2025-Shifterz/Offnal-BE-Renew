package com.offnal.shifterz.domain.memo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemo is a Querydsl query type for Memo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemo extends EntityPathBase<Memo> {

    private static final long serialVersionUID = -213366034L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemo memo = new QMemo("memo");

    public final com.offnal.shifterz.global.QBaseTimeEntity _super = new com.offnal.shifterz.global.QBaseTimeEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final NumberPath<Long> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.offnal.shifterz.domain.member.domain.QMember member;

    public final com.offnal.shifterz.domain.organization.domain.QOrganization organization;

    public final DatePath<java.time.LocalDate> targetDate = createDate("targetDate", java.time.LocalDate.class);

    public final StringPath title = createString("title");

    //inherited
    public final NumberPath<Long> updatedAt = _super.updatedAt;

    public QMemo(String variable) {
        this(Memo.class, forVariable(variable), INITS);
    }

    public QMemo(Path<? extends Memo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemo(PathMetadata metadata, PathInits inits) {
        this(Memo.class, metadata, inits);
    }

    public QMemo(Class<? extends Memo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.offnal.shifterz.domain.member.domain.QMember(forProperty("member")) : null;
        this.organization = inits.isInitialized("organization") ? new com.offnal.shifterz.domain.organization.domain.QOrganization(forProperty("organization"), inits.get("organization")) : null;
    }

}

