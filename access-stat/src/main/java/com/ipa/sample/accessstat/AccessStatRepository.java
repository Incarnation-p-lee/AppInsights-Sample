package com.ipa.sample.accessstat;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessStatRepository extends JpaRepository<AccessStatEntity, Long> {
}
