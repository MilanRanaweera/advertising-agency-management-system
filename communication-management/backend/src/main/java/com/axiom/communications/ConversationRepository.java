package com.axiom.communications;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository
  extends JpaRepository<Conversation, Long> {}
