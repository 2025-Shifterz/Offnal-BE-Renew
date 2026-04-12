package com.offnal.shifterz.domain.todo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTodo is a Querydsl query type for Todo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTodo extends EntityPathBase<Todo> {

    private static final long serialVersionUID = -1341210874L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTodo todo = new QTodo("todo");

    public final com.offnal.shifterz.global.QBaseTimeEntity _super = new com.offnal.shifterz.global.QBaseTimeEntity(this);

    public final BooleanPath completed = createBoolean("completed");

    public final StringPath content = createString("content");

    //inherited
    public final NumberPath<Long> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.offnal.shifterz.domain.member.domain.QMember member;

    public final com.offnal.shifterz.domain.organization.domain.QOrganization organization;

    public final DatePath<java.time.LocalDate> targetDate = createDate("targetDate", java.time.LocalDate.class);

    //inherited
    public final NumberPath<Long> updatedAt = _super.updatedAt;

    public QTodo(String variable) {
        this(Todo.class, forVariable(variable), INITS);
    }

    public QTodo(Path<? extends Todo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTodo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTodo(PathMetadata metadata, PathInits inits) {
        this(Todo.class, metadata, inits);
    }

    public QTodo(Class<? extends Todo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.offnal.shifterz.domain.member.domain.QMember(forProperty("member")) : null;
        this.organization = inits.isInitialized("organization") ? new com.offnal.shifterz.domain.organization.domain.QOrganization(forProperty("organization"), inits.get("organization")) : null;
    }

}

