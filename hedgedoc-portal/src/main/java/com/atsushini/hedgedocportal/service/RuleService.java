package com.atsushini.hedgedocportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.dto.FolderDto;
import com.atsushini.hedgedocportal.dto.RuleDto;
import com.atsushini.hedgedocportal.entity.Folder;
import com.atsushini.hedgedocportal.entity.Rule;
import com.atsushini.hedgedocportal.entity.User;
import com.atsushini.hedgedocportal.exception.NotFoundException;
import com.atsushini.hedgedocportal.repository.FolderRepository;
import com.atsushini.hedgedocportal.repository.RuleRepository;
import com.atsushini.hedgedocportal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RuleService {
    
    private final RuleRepository ruleRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    public List<RuleDto> getRules(CurrentUserDto userDto) {
        List<Rule> ruleDtos = ruleRepository.findByUserId(userDto.getId());

        return ruleDtos.stream().map(this::convertToDto).toList();
    }

    public void create(String title, String reqularExpression, Long folderId, CurrentUserDto currentUser) {
        Folder folder = folderRepository.findById(folderId).orElse(null);
        if (folder == null) throw new NotFoundException("Folder not found with ID: " + folderId);        

        Rule rule = new Rule();
        rule.setTitle(title);
        rule.setRegularExpression(reqularExpression);
        rule.setFolder(folder);

        User user = userRepository.findById(currentUser.getId()).orElse(null);
        rule.setUser(user);

        ruleRepository.save(rule);
    }

    private RuleDto convertToDto(Rule rule) {
        RuleDto dto = new RuleDto();
        dto.setId(rule.getId());
        dto.setTitle(rule.getTitle());
        dto.setRegularExpression(rule.getRegularExpression());

        FolderDto folder = new FolderDto();
        folder.setId(rule.getFolder().getId());
        folder.setTitle(rule.getFolder().getTitle());
        dto.setFolder(folder);

        return dto;
    }
}
