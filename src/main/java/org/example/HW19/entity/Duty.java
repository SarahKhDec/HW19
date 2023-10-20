package org.example.HW19.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Duty extends BaseEntity<Long> {

    String name;

    @OneToMany(mappedBy = "duty")
    List<UnderDuty> underDuties;
}
