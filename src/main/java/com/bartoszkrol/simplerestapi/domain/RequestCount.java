package com.bartoszkrol.simplerestapi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestCount implements Serializable {

    @Id
    @Column(name = "LOGIN")
    private String login;

    @Column(name = "REQUEST_COUNT")
    private Integer requestCount;

    public RequestCount incrementRequestCount() {
        requestCount += 1;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestCount that = (RequestCount) o;
        return login.equals(that.login) &&
                requestCount.equals(that.requestCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, requestCount);
    }

}
