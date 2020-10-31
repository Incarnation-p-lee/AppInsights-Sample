package com.ipa.sample.accessstat;

import com.ipa.sample.common.AccessStat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "access")
public class AccessStatEntity extends AccessStat {

    @Id
    private Long id;
}
