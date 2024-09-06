package org.dorandoran.dorandoran_backend.debateroom;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.dorandoran.dorandoran_backend.user.UserInfo;

@Entity
@Table
@NoArgsConstructor
public class DebateroomUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debateroom_no")  // 외래 키 컬럼 이름 설정
    private Debateroom debateroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")  // 외래 키 컬럼 이름 설정
    private UserInfo user;

    public DebateroomUser(Debateroom debateroom, UserInfo user) {
        this.debateroom = debateroom;
        this.user = user;
    }
}
