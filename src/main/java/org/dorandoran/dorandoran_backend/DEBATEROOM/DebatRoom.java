package org.dorandoran.dorandoran_backend.DEBATEROOM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor
public class DebatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;


}
